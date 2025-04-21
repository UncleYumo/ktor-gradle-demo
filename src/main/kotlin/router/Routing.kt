package router

import ApplicationContext
import cn.uncleyumo.utils.ColorPrinter
import cn.uncleyumo.utils.LogPrinter
import entity.ResultVo
import io.ktor.http.*
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import kotlinx.serialization.Serializable
import utils.requestLog
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author uncle_yumo
 * @fileName Demo
 * @createDate 2025/4/19 April
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

fun Application.demoRouting() {
    routing {
        route("/api") {
            get("/") {
                call.respondText("Hello, World!")
            }
            get("/test") {
                call.respond(ResultVo.data("Hello, World!"))
            }
            get("/ok") {
                call.respond(ResultVo.ok("成功啦！！！！！"))
            }
            get("/error") {
                call.respond(ResultVo.error("Error"))
            }
            get("/data") {
                call.respond(ResultVo.data(Customer(1, "John", "Doe")))
            }
            post<Customer>("/customer/{id}") {
                log.info("Received customer: $it")
                // 获取路径参数
                log.info("Received customer id: ${call.pathParameters["id"]}")
                call.respond(ResultVo.data(it))
            }
        }
    }
}

fun Application.swaggerRouting() {
    routing {
        swaggerUI(path = ApplicationContext.SWAGGER_UI_URL, swaggerFile = "openapi/documentation.yaml")
    }
}

fun Application.websocketRouting() {
    routing {
        val sessionsEsp32 = Collections.synchronizedList(ArrayList<WebSocketSession>())
        val sessionsClient = Collections.synchronizedList(ArrayList<WebSocketSession>())
        webSocket("/cam") {
            if (
                call.parameters["key"] !in
                    listOf(ApplicationContext.WEBSOCKET_CLIENT_KET, ApplicationContext.WEBSOCKET_ESP32_KET)
            ) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid key"))
                return@webSocket
            }
            when (call.parameters["key"]) {
                ApplicationContext.WEBSOCKET_CLIENT_KET -> {
                    sessionsClient.add(this)
                    log.info("新增一个Client WebSocket连接，当前连接数: ${sessionsClient.size}")
                }
                ApplicationContext.WEBSOCKET_ESP32_KET -> {
                    sessionsEsp32.add(this)
                    log.info("新增一个ESP32 WebSocket连接，当前连接数: ${sessionsEsp32.size}")
                }
            }
            try {
                for (frame in incoming) {
                    when (frame) {
                        // 修改后的Frame.Text处理部分
                        is Frame.Text -> {
                            val text = frame.readText()
                            log.info("接收到Text消息: $text")

                            // 新增Base64解码逻辑
                            if (text.startsWith("data:image/jpeg;base64,")) {
                                val base64Data = text.split(",")[1]
                                val bytes = Base64.getDecoder().decode(base64Data)

                                // 转发二进制帧给客户端
                                sessionsClient.forEach { client ->
                                    if (client.isActive) {
                                        client.send(Frame.Binary(true, bytes))
                                    }
                                }
                            } else {
                                // 原始文本消息处理
                                sessionsClient.forEach { client ->
                                    if (client.isActive) {
                                        client.send("ESP32: $text")
                                    }
                                }
                            }
                        }
                        // 修改后的Frame.Binary处理部分
                        is Frame.Binary -> {
                            val bytes = frame.readBytes()
                            // 直接转发二进制数据
                            sessionsClient.forEach { client ->
                                if (client.isActive) {
                                    client.outgoing.send(Frame.Binary(true, bytes))
                                }
                            }
                        }
                        else -> {
                            log.info("接收到其他消息: $frame")
                        }
                    }
                }
            } catch (e: ClosedReceiveChannelException) {
                log.info("WebSocket连接关闭")
            } finally {
                when (call.parameters["key"]) {
                    ApplicationContext.WEBSOCKET_CLIENT_KET -> {
                        sessionsClient.remove(this)
                        log.info("移除一个Client WebSocket连接，当前连接数: ${sessionsClient.size}")
                    }
                    ApplicationContext.WEBSOCKET_ESP32_KET -> {
                        sessionsEsp32.remove(this)
                        log.info("移除一个ESP32 WebSocket连接，当前连接数: ${sessionsEsp32.size}")
                    }
                }
            }
        }
    }
}

fun Application.templateRouting() {
    routing {
        staticResources(
            remotePath = "/",
            basePackage = "static",  // 资源目录（src/main/resources/static）
            index = "index.html"     // 默认文件
        )
    }
}

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)