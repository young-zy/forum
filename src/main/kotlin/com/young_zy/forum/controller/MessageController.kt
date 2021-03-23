package com.young_zy.forum.controller

import com.young_zy.forum.controller.request.PostMessageRequest
import com.young_zy.forum.controller.response.MessageResponse
import com.young_zy.forum.service.MessageService
import com.young_zy.forum.service.RateLimitService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
class MessageController {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var rateLimitService: RateLimitService

    @PostMapping("/message")
    suspend fun postMessage(@RequestBody requestBody: PostMessageRequest) {
        logger.info("request start")
        rateLimitService.buildHeader()
        messageService.postMessage(requestBody.to, requestBody.messageText)
        logger.info("request end")
    }

    @GetMapping("/message")
    suspend fun getMessage(@RequestParam page: Long?, @RequestParam size: Long?): ResponseEntity<MessageResponse> {
        rateLimitService.buildHeader()
        val body = MessageResponse(messageService.getAllMessage(page ?: 1, size ?: 10))
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(body)
    }

    @DeleteMapping("/message/{messageId}")
    suspend fun deleteMessage(@PathVariable messageId: Long) {
        rateLimitService.buildHeader()
        messageService.deleteMessage(messageId)
    }

    @PutMapping("/message/{messageId}")
    suspend fun updateMessageReadState(@PathVariable messageId: Long, @RequestParam state: Boolean) {
        rateLimitService.buildHeader()
        messageService.setReadState(messageId, state)
    }
}