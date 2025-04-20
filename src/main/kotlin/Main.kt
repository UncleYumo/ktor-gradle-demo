import cn.uncleyumo.utils.ColorPrinter
import cn.uncleyumo.utils.LogPrinter
import config.installLogger
import config.installSerial
import config.installTemplate
import config.installWebsocket
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import router.demoRouting
import router.swaggerRouting
import router.templateRouting
import router.websocketRouting

@Suppress("HttpUrlsUsage", "")
fun main(args: Array<String>) {
    ColorPrinter.printFontBlue("""
        🚀 ${ApplicationContext.SERVER_NAME} is running......
        Server is running on http://${ApplicationContext.SERVER_HOST}:${ApplicationContext.SERVER_PORT}
        ESP32 WebSocket URL: ws://${ApplicationContext.SERVER_HOST}:${ApplicationContext.SERVER_PORT}/cam?key=${ApplicationContext.WEBSOCKET_ESP32_KET}
        Client WebSocket URL: ws://${ApplicationContext.SERVER_HOST}:${ApplicationContext.SERVER_PORT}/cam?key=${ApplicationContext.WEBSOCKET_CLIENT_KET}
        
    """.trimIndent())
    embeddedServer(Netty, port = ApplicationContext.SERVER_PORT) {
        run()
    }.start(wait = true)
}

private fun Application.run() {
    // Below is the install code
    installSerial()
    installLogger()
    installWebsocket()
    installTemplate()
    // Below is the routing code
    demoRouting()
    swaggerRouting()
    websocketRouting()
    templateRouting()
}

object ApplicationContext {
    const val SERVER_PORT: Int = 8085
    const val SERVER_HOST: String = "localhost"
    const val SERVER_NAME: String = "ktor-graphql-demo"
    const val SWAGGER_UI_URL: String = "/swagger"
    const val WEBSOCKET_ESP32_KET: String = "esp32"
    const val WEBSOCKET_CLIENT_KET: String = "client"

}