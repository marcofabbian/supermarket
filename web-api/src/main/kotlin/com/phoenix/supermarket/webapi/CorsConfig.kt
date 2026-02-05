package com.phoenix.supermarket.webapi

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Global CORS configuration. Allowed origins can be configured via
 * `app.cors.allowed-origins` (comma-separated). If not provided, a sensible default
 * including common local dev origins is used.
 */
@Configuration
class CorsConfig(
    @Value("\${app.cors.allowed-origins:}")
    private val allowedOriginsRaw: String
) : WebMvcConfigurer {

    private val defaultOrigins = arrayOf(
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "http://localhost:8080",
        "http://127.0.0.1:8080"
    )

    private val allowedOrigins: Array<String>
        get() = if (allowedOriginsRaw.isBlank()) defaultOrigins else allowedOriginsRaw.split(',').map { it.trim() }.filter { it.isNotEmpty() }.toTypedArray()

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins(*allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*", "X-Total-Count")
            .exposedHeaders("X-Total-Count")
            .allowCredentials(false)
    }
}
