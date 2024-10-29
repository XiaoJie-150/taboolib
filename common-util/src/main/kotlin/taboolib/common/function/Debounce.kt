package taboolib.common.function

import taboolib.common.Inject
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import java.util.concurrent.*

abstract class DebounceFunction<K>(
    protected val delay: Long,
    protected val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    protected val autoShutdown: Boolean = true
) {
    protected val futureMap = ConcurrentHashMap<K, ScheduledFuture<*>>()

    fun removeKey(key: K) {
        futureMap.remove(key)?.cancel(false)
    }

    fun clearAll() {
        futureMap.values.forEach { it.cancel(false) }
        futureMap.clear()
    }

    fun shutdown() {
        clearAll()
        executor.shutdown()
    }

    class Simple<K>(
        delay: Long,
        executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
        private val action: (K) -> Unit,
        autoShutdown: Boolean = true
    ) : DebounceFunction<K>(delay, executor, autoShutdown) {

        init {
            addDebounceFunction(this)
        }

        operator fun invoke(key: K) {
            val future = executor.schedule({ action(key) }, delay, TimeUnit.MILLISECONDS)
            futureMap[key]?.cancel(false)
            futureMap[key] = future
        }
    }

    class Parameterized<K, T>(
        delay: Long,
        executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
        private val action: (K, T) -> Unit,
        autoShutdown: Boolean = true
    ) : DebounceFunction<K>(delay, executor, autoShutdown) {

        init {
            addDebounceFunction(this)
        }

        operator fun invoke(key: K, param: T) {
            val future = executor.schedule({ action(key, param) }, delay, TimeUnit.MILLISECONDS)
            futureMap[key]?.cancel(false)
            futureMap[key] = future
        }
    }

    @Inject
    companion object {

        private val allDebounceFunctions = CopyOnWriteArrayList<DebounceFunction<*>>()

        @Awake(LifeCycle.DISABLE)
        private fun onDisable() {
            allDebounceFunctions.forEach { debounceFunction ->
                if (debounceFunction.autoShutdown) {
                    debounceFunction.shutdown()
                }
            }
            allDebounceFunctions.clear()
        }

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
 * @param executor 自定义的执行器，默认使用单线程调度执行器
 * @param autoShutdown 是否在插件禁用时自动关闭执行器，默认为 true
 * @param action 要执行的操作
 */
fun <K> debounce(
    delay: Long,
    executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    autoShutdown: Boolean = true,
    action: (K) -> Unit
): DebounceFunction.Simple<K> {
    return DebounceFunction.Simple(delay, executor, action, autoShutdown)
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
 * @param executor 自定义的执行器，默认使用单线程调度执行器
 * @param autoShutdown 是否在插件禁用时自动关闭执行器，默认为 true
 * @param action 要执行的操作
 */
fun <K, T> debounce(
    delay: Long,
    executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
    autoShutdown: Boolean = true,
    action: (K, T) -> Unit
): DebounceFunction.Parameterized<K, T> {
    return DebounceFunction.Parameterized(delay, executor, action, autoShutdown)
}