package config

import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.request.*
import org.slf4j.event.Level

/**
 * @author uncle_yumo
 * @fileName LoggerConfig
 * @createDate 2025/4/19 April
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

fun Application.installLogger() {
    install(CallLogging) {
        level = Level.INFO
    }
}