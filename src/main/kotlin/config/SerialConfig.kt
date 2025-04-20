package config

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

/**
 * @author uncle_yumo
 * @fileName SerialConfig
 * @createDate 2025/4/19 April
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

fun Application.installSerial() {
    install(ContentNegotiation) {
        json(Json {
            isLenient = true  // ignore unknown keys in JSON
            prettyPrint = true // pretty print JSON
        })
    }
}