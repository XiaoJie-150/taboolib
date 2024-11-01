package taboolib.platform

import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import taboolib.common.Inject
import taboolib.common.function.DebounceFunction
import taboolib.common.function.ThrottleFunction
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer

@Inject
@PlatformSide(Platform.BUNGEE)
private class BukkitResources {

    @SubscribeEvent
    fun onQuit(e: PlayerDisconnectEvent) {
        ThrottleFunction.allThrottleFunctions.forEach {
            when (it.keyType) {
                ProxiedPlayer::class.java -> it.removeKey(e.player)
                ProxyPlayer::class.java -> it.removeKey(adaptPlayer(e.player))
            }
        }
        DebounceFunction.allDebounceFunctions.forEach {
            when (it.keyType) {
                ProxiedPlayer::class.java -> it.removeKey(e.player)
                ProxyPlayer::class.java -> it.removeKey(adaptPlayer(e.player))
            }
        }
    }
}