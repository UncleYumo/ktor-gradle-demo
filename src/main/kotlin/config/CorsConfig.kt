package config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

/**
 * @author uncle_yumo
 * @fileName CorsConfig
 * @createDate 2025/4/19 April
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

fun Application.installCORS() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)  // 允许 Content-Type 请求头
        allowHeader(HttpHeaders.Authorization)  // 允许 Authorization 请求头
        allowHeader(HttpHeaders.Origin)  // 允许 Origin 请求头
        allowHeader(HttpHeaders.AccessControlRequestHeaders)  // 允许 Access-Control-Request-Headers 请求头
        allowHeader(HttpHeaders.AccessControlRequestMethod)  // 允许 Access-Control-Request-Method 请求头
        allowCredentials = true  // 允许携带 cookie
    }
}