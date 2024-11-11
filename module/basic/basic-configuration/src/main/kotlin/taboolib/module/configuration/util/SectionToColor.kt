package taboolib.module.configuration.util

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.chat.colored

/**
 * 获取文本并上色
 *
 * @param node 路径
 * @return 上色后的文本
 */
fun ConfigurationSection.getStringColored(node: String): String? {
    val string = getString(node) ?: return null
    return runCatching { string.colored() }.getOrNull()
}

/**
 * 获取文本列表并上色
 *
 * @param node 路径
 * @return 上色后的文本
 */
fun ConfigurationSection.getStringListColored(node: String): List<String> {
    val stringList = getStringList(node)
    return runCatching { stringList.map { it.colored() } }.getOrElse { stringList }
}