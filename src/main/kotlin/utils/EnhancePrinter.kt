package utils

import cn.uncleyumo.utils.LogPrinter

/**
 * @author uncle_yumo
 * @fileName EnhancePrinter
 * @createDate 2025/4/19 April
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

fun requestLog(
    method: String,
    url: String,
    params: Any? = null,
) {
    val builder = StringBuilder()
    builder.append("Request: $method\t$url\t")
    if (params != null) builder.append("params: $params\t")
    LogPrinter.info(builder.toString())
}

