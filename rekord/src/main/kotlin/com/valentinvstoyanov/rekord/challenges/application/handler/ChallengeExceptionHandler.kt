package com.valentinvstoyanov.rekord.challenges.application.handler

import com.valentinvstoyanov.rekord.ApiError
import com.valentinvstoyanov.rekord.ApiSubError
import com.valentinvstoyanov.rekord.challenges.domain.model.ChallengeException
import com.valentinvstoyanov.rekord.challenges.domain.model.ChallengeValidationException
import com.valentinvstoyanov.rekord.HandlerStrategiesResponseContext
import com.valentinvstoyanov.rekord.challenges.domain.model.ChallengeNotFoundException
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class ChallengeExceptionHandler : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        if (ex !is ChallengeException)
            return ex.toMono()

        val handled = when(ex) {
            is ChallengeValidationException -> ServerResponse.badRequest()
                .bodyValue(ApiError(HttpStatus.BAD_REQUEST.value(), "Challenge validation failed", listOf(ApiSubError("challenges", "Challenge fields validation failed", ex.hint))))
            is ChallengeNotFoundException -> ServerResponse.badRequest()
                .bodyValue(ApiError(HttpStatus.BAD_REQUEST.value(), "Challenge not found", listOf(ApiSubError("challenges", "Challenge was not found", "Failed to find challenge with id = ${ex.id}"))))
        }

        return handled.flatMap { it.writeTo(exchange, HandlerStrategiesResponseContext(HandlerStrategies.withDefaults())) }
    }
}