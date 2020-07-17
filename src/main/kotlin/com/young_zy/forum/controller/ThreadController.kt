package com.young_zy.forum.controller

import com.young_zy.forum.controller.request.PostThreadRequest
import com.young_zy.forum.controller.request.ReplyRequest
import com.young_zy.forum.controller.response.*
import com.young_zy.forum.service.HitRateService
import com.young_zy.forum.service.RateLimitService
import com.young_zy.forum.service.ThreadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class ThreadController {
    @Autowired
    lateinit var rateLimitService: RateLimitService

    @Autowired
    lateinit var threadService: ThreadService

    @Autowired
    lateinit var hitRateService: HitRateService

    @PostMapping("/thread/{threadId}/reply")
    suspend fun postReply(@RequestHeader headers: Map<String, String>,
                          @PathVariable threadId: Long,
                          @RequestBody replyRequest: ReplyRequest): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        threadService.postReply(headers["token"] ?: "", threadId, replyRequest.replyContent)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PostMapping("/thread")
    suspend fun postThread(
            @RequestHeader headers: Map<String, String>,
            @RequestBody requestBody: PostThreadRequest
    ): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        threadService.postThread(
                headers["token"],
                requestBody.sectionId,
                requestBody.title,
                requestBody.content,
                requestBody.isQuestion
        )
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @GetMapping("/thread/{threadId}")
    suspend fun getThread(
            @RequestHeader headers: Map<String, String>,
            @PathVariable("threadId") threadId: Long,
            @RequestParam("page") page: Int?,
            @RequestParam("size") size: Int?,
            @RequestParam("orderBy") orderBy: String?
    ): ResponseEntity<*> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        val thread = threadService.getThread(headers["token"] ?: "", threadId, page ?: 1, size ?: 10, orderBy
                ?: "priority")
        responseBody = ThreadResponse(thread)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PutMapping("/reply/{replyId}/vote")
    suspend fun updateVote(@PathVariable replyId: Long, @RequestHeader headers: Map<String, String>, @RequestParam state: Long): ResponseEntity<Response> {
        val responseBody: Response? = null
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        threadService.vote(headers["token"] ?: "", replyId, state)
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PutMapping("/reply/{replyId}")
    suspend fun updateReply(@PathVariable replyId: Long,
                            @RequestHeader headers: Map<String, String>,
                            @RequestBody requestBody: ReplyRequest): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        threadService.updateReply(headers["token"] ?: "", replyId, requestBody.replyContent)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @DeleteMapping("/reply/{replyId}")
    suspend fun deleteReply(@PathVariable replyId: Long, @RequestHeader headers: Map<String, String>): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        threadService.deleteReply(headers["token"] ?: "", replyId)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @DeleteMapping("/thread/{threadId}")
    suspend fun deleteThread(@PathVariable threadId: Long, @RequestHeader headers: Map<String, String>): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        threadService.deleteThread(headers["token"] ?: "", threadId)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @GetMapping("/reply/{replyId}")
    suspend fun getReply(@PathVariable replyId: Int, @RequestHeader headers: Map<String, String>): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        responseBody = ReplyResponse(threadService.getReply(headers["token"] ?: "", replyId))
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @GetMapping("/search")
    suspend fun search(@RequestParam keyWord: String, @RequestHeader headers: Map<String, String>,
                       @RequestParam size: Long?, @RequestParam page: Long?): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        responseBody = SearchResponse(threadService.search(headers["token"] ?: "", keyWord, page ?: 1, size ?: 10))
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @GetMapping("/thread/hot")
    suspend fun getHotThreads(@RequestHeader headers: Map<String, String>, @RequestParam count: Int?): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        responseBody = HitRateResponse(hitRateService.getOrder(count ?: 20))
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }
}