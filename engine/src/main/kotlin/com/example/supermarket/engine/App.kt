package com.example.supermarket.engine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringbootApplication
class EngineApplication

fun main(args: Array<String>) {
    runApplication<EngineApplication>(*args)
}
