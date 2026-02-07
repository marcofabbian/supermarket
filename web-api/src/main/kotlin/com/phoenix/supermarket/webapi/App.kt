package com.phoenix.supermarket.webapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SupermarketWebApiApplication

fun main(args: Array<String>) {
    runApplication<SupermarketWebApiApplication>(*args)
}
