package bot.boobbot.internals

import bot.boobbot.BoobBot
import net.dv8tion.jda.api.utils.SessionController.SessionConnectNode
import net.dv8tion.jda.api.utils.SessionControllerAdapter

class ExtendedSessionController(bucketFactor: Int = 16) : SessionControllerAdapter() {
    private val controllers: List<SessionController>

    init {
        BoobBot.log.info("Using $bucketFactor SessionControllers.")
        this.controllers = (0 until bucketFactor).map { SessionController() }
    }

    override fun appendSession(node: SessionConnectNode) {
        controllerFor(node).appendSession(node)
    }

    override fun removeSession(node: SessionConnectNode) {
        controllerFor(node).removeSession(node)
    }

    private fun controllerFor(node: SessionConnectNode): SessionController {
        return controllers[node.shardInfo.shardId % controllers.size]
    }
}
