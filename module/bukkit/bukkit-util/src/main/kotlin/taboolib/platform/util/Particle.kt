package taboolib.platform.util

import org.bukkit.Particle
import taboolib.common.platform.ProxyParticle

fun ProxyParticle.toBukkit(): Particle {
    return toBukkitOrNull() ?: error("Unsupported: $name")
}

fun ProxyParticle.toBukkitOrNull(): Particle? {
    return Particle.values().find { it.name == name || it.name in aliases }
}