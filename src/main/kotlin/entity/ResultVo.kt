package entity

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @author uncle_yumo
 * @fileName ResultVo
 * @createDate 2025/4/18 April
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@Serializable
data class ResultVo<T>(
    val code: Int,
    var message: String,
    var data: T?
) {
    companion object {
        fun ok(message: String = "ok") = ResultVo(0, message, null)
        fun <T> data(data: T) = ResultVo(0, "ok", data)
        fun error(message: String = "error occurred") = ResultVo(-1, message, null)
    }
}
