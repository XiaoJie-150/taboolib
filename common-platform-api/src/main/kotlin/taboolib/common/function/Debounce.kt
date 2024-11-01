package taboolib.common.function

import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

abstract class DebounceFunction<K : Any>(
    val keyType: Class<K>,
    val delay: Long,
    val async: Boolean = false,
) {
    val futureMap = ConcurrentHashMap<K, PlatformExecutor.PlatformTask>()

    fun removeKey(key: Any) {
        futureMap.remove(key)?.cancel()
    }

    fun clearAll() {
        futureMap.values.forEach { it.cancel() }
        futureMap.clear()
    }

    fun shutdown() {
        clearAll()
    }

    class Simple<K : Any>(
        keyType: Class<K>,
        delay: Long,
        async: Boolean = false,
        val action: (K) -> Unit,
    ) : DebounceFunction<K>(keyType, delay, async) {

        init {
            addDebounceFunction(this)
        }

        operator fun invoke(key: K) {
            val future = submit(async = async, delay = delay / 50) { action(key) }
            futureMap[key]?.cancel()
            futureMap[key] = future
        }
    }

    class Parameterized<K : Any, T>(
        keyType: Class<K>,
        delay: Long,
        async: Boolean = false,
        val action: (K, T) -> Unit,
    ) : DebounceFunction<K>(keyType, delay, async) {

        init {
            addDebounceFunction(this)
        }

        operator fun invoke(key: K, param: T) {
            val future = submit(async = async, delay = delay / 50) { action(key, param) }
            futureMap[key]?.cancel()
            futureMap[key] = future
        }
    }

    companion object {

        val allDebounceFunctions = CopyOnWriteArrayList<DebounceFunction<*>>()

        fun addDebounceFunction(debounceFunction: DebounceFunction<*>) {
            allDebounceFunctions.add(debounceFunction)
        }
    }
}

/**
 * 创建基础防抖函数：
 * 可以全局使用，也可以针对特定对象（如玩家）使用。在指定时间内只执行一次函数，如果在这段时间内再次调用函数，则重新计时。
 *
 * 示例：
 * ```kotlin
 * // 创建一个 500 毫秒的防抖函数
 * val debouncedAction = debounce<Player>(500) { player ->
 *     println("玩家 ${player.name} 的防抖后输出")
 * }
 *
 * // 连续调用
 * debouncedAction(player)
 * debouncedAction(player) // 重置计时
 * debouncedAction(player) // 重置计时
 *
 * // 等待 600 毫秒
 * Thread.sleep(600)
 *
 * // 最终只会输出一次：
 * // 玩家 player 的防抖后输出
 * ```
 *
 * @param K 键类型（可以是 Player 或其他对象类型）
 * @param delay 防抖时间（单位：毫秒）
 * @param async 是否在异步线程执行，取决于当前线程
 * @param action 要执行的操作
 */
inline fun <reified K : Any> debounce(
    delay: Long,
    async: Boolean = !isPrimaryThread,
    noinline action: (K) -> Unit = { _ -> },
): DebounceFunction.Simple<K> {
    return DebounceFunction.Simple(K::class.java, delay, async, action)
}

/**
 * 创建带参数的防抖函数：
 * 可以全局使用，也可以针对特定对象（如玩家）使用。在指定时间内只执行一次函数，如果在这段时间内再次调用函数，则重新计时。
 * 与基础版本不同的是，这个版本可以传递额外的参数。
 *
 * 示例：
 * ```kotlin
 * // 创建一个 500 毫秒的防抖函数
 * val debouncedAction = debounce<Player, String>(500) { player, message ->
 *     println("玩家 ${player.name} 的防抖后输出：$message")
 * }
 *
 * // 连续调用
 * debouncedAction(player, "消息1")
 * debouncedAction(player, "消息2") // 重置计时
 * debouncedAction(player, "消息3") // 重置计时
 *
 * // 等待 600 毫秒
 * Thread.sleep(600)
 *
 * // 最终只会输出一次：
 * // 玩家 player 的防抖后输出：消息3
 * ```
 *
 * @param K 键类型（可以是 Player 或其他对象类型）
 * @param T 参数类型
 * @param delay 防抖时间（单位：毫秒）
 * @param async 是否在异步线程执行，取决于当前线程
 * @param action 要执行的操作
 */
inline fun <reified K : Any, T> debounce(
    delay: Long,
    async: Boolean = !isPrimaryThread,
    noinline action: (K, T) -> Unit = { _, _ -> },
): DebounceFunction.Parameterized<K, T> {
    return DebounceFunction.Parameterized(K::class.java, delay, async, action)
}