package com.young_zy.forum.controller

import com.young_zy.forum.controller.request.PostMessageRequest
import com.young_zy.forum.controller.response.MessageResponse
import com.young_zy.forum.service.MessageService
import com.young_zy.forum.service.RateLimitService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class MessageController {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var rateLimitService: RateLimitService

    @PostMapping("/message")
    suspend fun postMessage(@RequestHeader headers: Map<String, String>, @RequestBody requestBody: PostMessageRequest){
        logger.info("request start")
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        messageService.postMessage(headers["token"] ?: "", requestBody.to, requestBody.messageText)
        logger.info("request end")
    }

    @GetMapping("/message")
    suspend fun getMessage(@RequestHeader headers: Map<String, String>, @RequestParam page: Long?, @RequestParam size: Long?): ResponseEntity<MessageResponse> {
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        val body = MessageResponse(messageService.getAllMessage(headers["token"] ?: "", page ?: 1, size ?: 10))
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(body)
    }

    @DeleteMapping("/message/{messageId}")
    suspend fun deleteMessage(@RequestHeader headers: Map<String, String>, @PathVariable messageId: Long){
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        messageService.deleteMessage(headers["token"] ?: "", messageId)
    }

    @PutMapping("/message/{messageId}")
    suspend fun updateMessageReadState(@RequestHeader headers: Map<String, String>, @PathVariable messageId: Long, @RequestParam state: Boolean){
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        messageService.setReadState(headers["token"] ?: "", messageId, state)
    }
}