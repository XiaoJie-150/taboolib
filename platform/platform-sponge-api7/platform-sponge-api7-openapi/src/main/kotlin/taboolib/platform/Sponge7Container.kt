package taboolib.platform

import org.spongepowered.api.plugin.PluginContainer
import taboolib.common.OpenContainer
import taboolib.common.OpenResult
import org.tabooproject.reflex.Reflex.Companion.invokeMethod
import taboolib.internal.Internal

/**
 * TabooLib
 * taboolib.platform.type.SpongeOpenContainer
 *
 * @author sky
 * @since 2021/7/3 1:44 上午
 */
@Internal
class Sponge7Container(plugin: PluginContainer) : OpenContainer {

    private val name = plugin.id
    private val main = plugin.instance.get().javaClass.name
    private val clazz = try {
        Class.forName(main.substring(0, main.length - "platform.Sponge7Plugin".length) + "common.OpenAPI")
    } catch (ignored: Throwable) {
        null
    }

    override fun getName(): String {
        return name
    }

    override fun call(name: String, args: Array<Any>): OpenResult {
        return try {
            OpenResult.deserialize(clazz?.invokeMethod<Any>("call", name, args, isStatic = true) ?: return OpenResult.failed())
        } catch (ignored: NoSuchMethodException) {
            OpenResult.failed()
        }
    }
}