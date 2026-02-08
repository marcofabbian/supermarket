package com.phoenix.supermarket.engine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringbootApplication(
    classpath = ["com.phoenix.supermarket.engine"]
)
class EngineApplication

fun main(args: Array<String>) {
    runApplication<EngineApplication>(*args)
}