package taboolib.module.configuration.util

import taboolib.common.util.t
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.SimpleComponent
import taboolib.module.chat.component

/**
 * 获取 [SimpleComponent]
 *
 * @param node 路径
 * @return [SimpleComponent]
 */
fun ConfigurationSection.getComponent(node: String): SimpleComponent? {
    return runCatching { getString(node)?.component() }.getOrElse { error(
        """
            缺少聊天模块 (需要 "MinecraftChat" 模块)
            Missing chat module (require "MinecraftChat" module)
        """.t()
    ) }
}

/**
 * 获取 [SimpleComponent]
 *
 * @param node 路径
 * @return [SimpleComponent]
 */
fun ConfigurationSection.getComponentToRaw(node: String): String? {
    return runCatching { getString(node)?.component()?.buildToRaw() }.getOrElse { error(
        """
            缺少聊天模块 (需要 "MinecraftChat" 模块)
            Missing chat module (require "MinecraftChat" module)
        """.t()
    ) }
}