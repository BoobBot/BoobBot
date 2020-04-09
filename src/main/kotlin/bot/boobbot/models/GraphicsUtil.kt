package bot.boobbot.models

import bot.boobbot.BoobBot
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class GraphicsUtil(imageUrl: String, options: Graphics2D.() -> Unit = {}) {

    private val image = BoobBot.requestUtil.get(imageUrl)
        .submit()
        .thenApply { it.body()?.byteStream() ?: throw IllegalStateException("ResponseBody is null") }
        .thenApply { it.use(ImageIO::read) }

    private var graphics = image.thenApply { it.createGraphics() }
        .thenApply { it.apply(options) }

    fun setBgColor(color: Color): GraphicsUtil {
        graphics = graphics.thenApply { it.background = color; it }
        return this
    }

    fun setForeColor(color: Color): GraphicsUtil {
        graphics = graphics.thenApply { it.color = color; it }
        return this
    }

    fun setFont(font: Font): GraphicsUtil {
        graphics = graphics.thenApply { it.font = font; it }
        return this
    }

    fun applyCustom(task: (Graphics2D) -> Unit): GraphicsUtil {
        graphics = graphics.thenApply { task(it); it }
        return this
    }

    fun applyCustomWithImage(task: (BufferedImage, Graphics2D) -> Unit): GraphicsUtil {
        graphics = graphics.thenCombine(image) { g, i -> task(i, g); g }
        return this
    }

    fun drawRectangle(x: Int, y: Int, width: Int, height: Int, color: Color? = null): GraphicsUtil {
        graphics = graphics.thenApply {
            color?.let(it::setColor)
            it.drawRect(x, y, width, height)
            it
        }
        return this
    }

    fun render(format: String = "png"): CompletableFuture<ByteArrayOutputStream> {
        return graphics.thenAccept(Graphics2D::dispose)
            .thenCompose { image }
            .thenApply {
                val stream = ByteArrayOutputStream()
                ImageIO.setUseCache(false)
                ImageIO.write(it, format, stream)

                stream
            }
    }
}
