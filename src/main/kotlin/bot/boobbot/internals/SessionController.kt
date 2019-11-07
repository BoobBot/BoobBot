package bot.boobbot.internals

import com.neovisionaries.ws.client.OpeningHandshakeException
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.exceptions.AccountTypeException
import net.dv8tion.jda.api.requests.Request
import net.dv8tion.jda.api.requests.Response
import net.dv8tion.jda.api.utils.SessionController
import net.dv8tion.jda.api.utils.SessionControllerAdapter
import net.dv8tion.jda.internal.requests.RestActionImpl
import net.dv8tion.jda.internal.requests.Route
import net.dv8tion.jda.internal.utils.tuple.Pair
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong
import javax.security.auth.login.LoginException

class SessionController : SessionController {

    private val globalRatelimit = AtomicLong(Long.MIN_VALUE)
    private val sessionManagers = hashMapOf<Int, SessionManager>()

    override fun getGlobalRatelimit() = globalRatelimit.get()
    override fun setGlobalRatelimit(ratelimit: Long) = globalRatelimit.set(ratelimit)

    override fun appendSession(node: SessionController.SessionConnectNode) {
        val managerId = node.shardInfo.shardId % 16
        val manager = sessionManagers.computeIfAbsent(managerId) { SessionManager(it) }
        manager.appendSession(node)
    }

    override fun removeSession(node: SessionController.SessionConnectNode) {
        val managerId = node.shardInfo.shardId % 16
        sessionManagers[managerId]?.removeSession(node)
    }

    override fun getGateway(api: JDA): String {
        val route = Route.Misc.GATEWAY.compile()
        return RestActionImpl<String>(api, route) { response, _ ->
            response.getObject().getString("url")
        }.complete()
    }

    override fun getGatewayBot(api: JDA): Pair<String, Int> {
        val bot = getShardedGateway(api)
        return Pair.of(bot.url, bot.shardTotal)
    }

    override fun getShardedGateway(api: JDA): SessionController.ShardedGateway {
        AccountTypeException.check(api.accountType, AccountType.BOT)
        return object : RestActionImpl<SessionController.ShardedGateway>(api, Route.Misc.GATEWAY_BOT.compile()) {
            override fun handleResponse(response: Response, request: Request<SessionController.ShardedGateway>) {
                try {
                    when {
                        response.isOk -> {
                            val `object` = response.getObject()
                            val url = `object`.getString("url")
                            val shards = `object`.getInt("shards")
                            request.onSuccess(SessionController.ShardedGateway(url, shards))
                        }
                        response.code == 401 -> this.api.get().verifyToken(true)
                        else -> request.onFailure(LoginException("When verifying the authenticity of the provided token, Discord returned an unknown response:\n$response"))
                    }
                } catch (e: Exception) {
                    request.onFailure(e)
                }
            }
        }.complete()
    }

    class SessionWorker(
        private val manager: SessionManager,
        private val delay: Long = 10000
        // 10 seconds. This takes 130 seconds (2 minutes 10 seconds) to boot 208 shards.
        // This should be 5 seconds (1 minute 5 seconds) but a low delay makes the server shit itself.
    ) : Thread("Session-Worker-${manager.id}") {

        init {
            super.setUncaughtExceptionHandler { thread, exception -> this.handleFailure(thread, exception) }
        }

        private fun handleFailure(thread: Thread, exception: Throwable) {
            log.error("Worker has failed with throwable!", exception)
        }

        override fun run() {
            try {
                if (delay > 0) {
                    val interval = System.currentTimeMillis() - manager.lastConnect
                    if (interval < delay) {
                        sleep(delay - interval)
                    }
                }
            } catch (ex: InterruptedException) {
                log.error("Unable to backoff", ex)
            }

            processQueue()
            synchronized(manager.lock) {
                manager.worker = null

                if (manager.connectQueue.isNotEmpty()) {
                    manager.runWorker()
                }
            }
        }

        private fun processQueue() {
            var isMultiple = manager.connectQueue.size > 1
            while (manager.connectQueue.isNotEmpty()) {
                val node = manager.connectQueue.poll()
                try {
                    node.run(isMultiple && manager.connectQueue.isEmpty())
                    isMultiple = true
                    manager.lastConnect = System.currentTimeMillis()
                    if (manager.connectQueue.isEmpty())
                        break
                    if (this.delay > 0)
                        sleep(this.delay)
                } catch (e: IllegalStateException) {
                    val t = e.cause
                    if (t is OpeningHandshakeException)
                        log.error("Failed opening handshake, appending to queue. Message: {}", e.message)
                    else
                        log.error("Failed to establish connection for a node, appending to queue", e)
                    manager.appendSession(node)
                } catch (e: InterruptedException) {
                    log.error("Failed to run node", e)
                    manager.appendSession(node)
                    return  // caller should start a new thread
                }
            }
        }
    }

    inner class SessionManager(val id: Int) {
        val connectQueue = ConcurrentLinkedQueue<SessionController.SessionConnectNode>()
        var lastConnect = 0L
        val lock = Object()
        var worker: Thread? = null

        fun appendSession(node: SessionController.SessionConnectNode) {
            connectQueue.add(node)
            runWorker()
        }

        fun removeSession(node: SessionController.SessionConnectNode) {
            connectQueue.remove(node)
        }

        fun runWorker() {
            synchronized(lock) {
                if (worker == null) {
                    worker = SessionWorker(this)
                    worker?.start()
                }
            }
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(SessionController::class.java)!!
    }

}
