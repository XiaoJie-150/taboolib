package taboolib.common.function

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

abstract class ThrottleFunction<K : Any>(
    val keyType: Class<K>,
    val delay: Long,
) {

    val throttleMap = ConcurrentHashMap<K, Long>()

    fun canExecute(key: K): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastExecuteTime = throttleMap.getOrDefault(key, 0L)
        return if (currentTime - lastExecuteTime >= delay) {
            throttleMap[key] = currentTime
            true
        } else false
    }

    fun removeKey(key: Any) {
        throttleMap.remove(key)
    }

    fun clearAll() {
        throttleMap.clear()
    }

    class Simple<K : Any>(
        keyType: Class<K>,
        delay: Long,
        val action: (K) -> Unit,
    ) : ThrottleFunction<K>(keyType, delay) {

        init {
            addThrottleFunction(this)
        }

        operator fun invoke(key: K) {
            if (canExecute(key)) action(key)
        }
    }

    class Parameterized<K : Any, T>(
        keyType: Class<K>,
        delay: Long,
        val action: (K, T) -> Unit,
    ) : ThrottleFunction<K>(keyType, delay) {

        init {
            addThrottleFunction(this)
        }

        operator fun invoke(key: K, param: T) {
            if (canExecute(key)) action(key, param)
        }
    }

    companion object {

        // 所有被创建的节流函数
        val allThrottleFunctions = CopyOnWriteArrayList<ThrottleFunction<*>>()

        // 添加节流函数到列表
        fun addThrottleFunction(throttleFunction: ThrottleFunction<*>) {
            allThrottleFunctions.add(throttleFunction)
        }
    }
}

/**
 * 创建基础节流函数：
 * 可以全局使用，也可以针对特定对象（如玩家）使用。在指定时间内只执行一次函数，忽略这段时间内的重复调用。
 *
 * 示例：
 * ```kotlin
 * // 创建一个 500 毫秒的节流函数
 * val throttledAction = throttle<Player>(500) { player ->
 *     println("玩家 ${player.name} 的节流后输出")
 * }
 *
 * // 连续调用
 * throttledAction(player)
 * throttledAction(player) // 会被忽略
 * throttledAction(player) // 会被忽略
 *
 * // 等待 600 毫秒后
 * Thread.sleep(600)
 * throttledAction(player) // 会被执行
 *
 * // 最终只会输出两次：
 * // 玩家 player 的节流后输出
 * // 玩家 player 的节流后输出
 * ```
 *
 * @param K 键类型（可以是 Player 或其他对象类型）
 * @param delay 节流时间（单位：毫秒）
 * @param action 要执行的操作
 */
inline fun <reified K : Any> throttle(delay: Long, noinline action: (K) -> Unit): ThrottleFunction.Simple<K> {
    return ThrottleFunction.Simple(K::class.java, delay, action)
}

/**
 * 创建带参数的节流函数：
 * 可以全局使用，也可以针对特定对象（如玩家）使用。在指定时间内只执行一次函数，忽略这段时间内的重复调用。
 * 与基础版本不同的是，这个版本可以传递额外的参数。
 *
 * 示例：
 * ```kotlin
 * // 创建一个 500 毫秒的节流函数
 * val throttledAction = throttle<Player, String>(500) { player, message ->
 *     println("玩家 ${player.name} 的节流后输出：$message")
 * }
 *
 * // 连续调用
 * throttledAction(player, "消息1")
 * throttledAction(player, "消息2") // 会被忽略
 * throttledAction(player, "消息3") // 会被忽略
 *
 * // 等待 600 毫秒后
 * Thread.sleep(600)
 * throttledAction(player, "消息4") // 会被执行
 *
 * // 最终只会输出两次：
 * // 玩家 player 的节流后输出：消息1
 * // 玩家 player 的节流后输出：消息4
 * ```
 *
 * @param K 键类型（可以是 Player 或其他对象类型）
 * @param T 参数类型
 * @param delay 节流时间（单位：毫秒）
 * @param action 要执行的操作
 */
inline fun <reified K : Any, T> throttle(delay: Long, noinline action: (K, T) -> Unit): ThrottleFunction.Parameterized<K, T> {
    return ThrottleFunction.Parameterized(K::class.java, delay, action)
}