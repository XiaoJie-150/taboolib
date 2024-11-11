package taboolib.module.lang.gameside

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.util.replaceWithOrder
import taboolib.module.lang.Type

/**
 * TabooLib
 * taboolib.module.lang.gameside.TypeActionBar
 *
 * @author sky
 * @since 2021/6/20 10:55 下午
 */
class TypeActionBar : Type {

    lateinit var text: String

    override fun init(source: Map<String, Any>) {
        text = source["text"].toString()
    }

    override fun send(sender: ProxyCommandSender, vararg args: Any) {
        val newText = text.translate(sender, *args).replaceWithOrder(*args)
        if (sender is ProxyPlayer) {
            sender.sendActionBar(newText)
        } else {
            sender.sendMessage(newText)
        }
    }

    override fun toString(): String {
        return "NodeActionBar(text='$text')"
    }
}