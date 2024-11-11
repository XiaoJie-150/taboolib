package taboolib.platform.type

import net.md_5.bungee.api.plugin.Cancellable
import net.md_5.bungee.api.plugin.Event
import taboolib.common.PrimitiveIO.t
import taboolib.platform.BungeePlugin

open class BungeeProxyEvent : Event(), Cancellable {

    private var isCancelled = false

    open val allowCancelled: Boolean
        get() = true

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(value: Boolean) {
        if (allowCancelled) {
            isCancelled = value
        } else {
            error(t("这个事件无法被取消。", "This event cannot be cancelled."))
        }
    }

    fun call(): Boolean {
        BungeePlugin.getInstance().proxy.pluginManager.callEvent(this)
        return !isCancelled
    }
}