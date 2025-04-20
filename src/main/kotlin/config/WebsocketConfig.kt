package config

import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

/**
 * @author uncle_yumo
 * @fileName WebsocketConfig
 * @createDate 2025/4/19 April
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

fun Application.installWebsocket() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)  // 使用kotlinx序列化库作为websocket消息的编解码器
        pingPeriod = 60.seconds  // 发送ping消息的时间间隔
        timeout = 15.seconds  // 超时时间
        maxFrameSize = Long.MAX_VALUE  // 最大帧大小
        masking = false  // 配置是否对发送的数据进行掩码处理
    }
}