package com.young_zy.forum.controller

import com.young_zy.forum.controller.response.Response
import com.young_zy.forum.service.exception.AuthException
import com.young_zy.forum.service.exception.NotAcceptableException
import com.young_zy.forum.service.exception.NotFoundException
import com.young_zy.forum.service.exception.RateLimitExceededException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.io.PrintWriter
import java.io.StringWriter

@RestControllerAdvice
class ExceptionHandler {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    val Exception.stackTraceString: String
        get() {
            val stringWriter = StringWriter()
            this.printStackTrace(PrintWriter(stringWriter))
            return stringWriter.toString()
        }

    @ExceptionHandler(RateLimitExceededException::class)
    fun handleRateLimitExceededException(e: RateLimitExceededException): ResponseEntity<Response> {
        return ResponseEntity.status(429).body(
                Response(false, e.message)
        )
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<Any> {
        return ResponseEntity.status(404).body(
                Response(
                        false,
                        e.message
                )
        )
    }

    @ExceptionHandler(NotAcceptableException::class)
    fun handleNotAcceptableException(e: NotAcceptableException): ResponseEntity<Any> {
        return ResponseEntity.status(406).body(
                Response(
                        false,
                        e.message
                )
        )
    }

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(e: AuthException): ResponseEntity<Any> {
        return ResponseEntity.status(403).body(
                Response(
                        false,
                        e.message
                )
        )
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleDefaultException(e: ResponseStatusException): ResponseEntity<Any> {
        return ResponseEntity.status(e.status)
                .headers(e.responseHeaders)
                .body(
                        Response(
                                false,
                                e.localizedMessage
                        )
                )
    }

    @ExceptionHandler(Exception::class)
    fun handleOtherException(e: Exception): ResponseEntity<Any> {
        logger.error(e.stackTraceString)
        return ResponseEntity.status(500).body(
                Response(
                        false,
                        e.message ?: ""
                )
        )
    }

}