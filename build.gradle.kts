plugins {
    kotlin("jvm").version("2.0.0-RC1")
    kotlin("plugin.serialization").version("2.0.0-RC1")
    application
}

group = "cn.uncleyumo"
version = "0.0.1"

val ktorVersion = "3.0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("cn.uncleyumo.utils:print-plus-kotlin:1.1.0")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.netty:netty-tcnative-boringssl-static:2.0.62.Final")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("io.ktor:ktor-server-thymeleaf:$ktorVersion")

    testImplementation(kotlin("test"))
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
    sourceSets.all {
        languageSettings.enableLanguageFeature("ContextReceivers")
    }
}

application {
    mainClass.set("MainKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

// 在文件尾部添加以下任务配置
tasks {
    val fatJar = register<Jar>("fatJar") {
        archiveClassifier.set("all")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest {
            attributes["Main-Class"] = application.mainClass.get()
        }

        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map { zipTree(it).matching {
                    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA") // 排除签名文件
                }}
        })
    }

    build {
        dependsOn(fatJar)
    }
}