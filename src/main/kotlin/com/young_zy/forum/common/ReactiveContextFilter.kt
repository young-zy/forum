package com.young_zy.forum.common

import org.springframework.web.server.WebFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import org.springframework.web.server.WebFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Configuration
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
class ReactiveContextFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val response = exchange.response
        return chain.filter(exchange)
            .contextWrite { ctx ->
                ctx.put(
                    ServerHttpRequest::class.java,
                    request
                ).put(
                    ServerHttpResponse::class.java,
                    response
                )
            }
    }
}