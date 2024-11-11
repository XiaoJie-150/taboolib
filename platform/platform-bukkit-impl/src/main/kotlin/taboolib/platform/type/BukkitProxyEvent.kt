package taboolib.platform.type

import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import taboolib.common.PrimitiveIO.t

@Suppress("SameReturnValue")
open class BukkitProxyEvent : Event(!Bukkit.isPrimaryThread()), Cancellable {

    private var isCancelled = false

    open val allowCancelled: Boolean
        get() = true

    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }

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
        Bukkit.getPluginManager().callEvent(this)
        return !isCancelled
    }

    companion object {

        @JvmField
        val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }
}