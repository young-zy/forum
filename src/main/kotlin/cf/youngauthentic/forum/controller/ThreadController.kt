package cf.youngauthentic.forum.controller

import cf.youngauthentic.forum.controller.request.PostReplyRequest
import cf.youngauthentic.forum.controller.request.PostThreadRequest
import cf.youngauthentic.forum.controller.request.UpdateReplyRequest
import cf.youngauthentic.forum.controller.response.Response
import cf.youngauthentic.forum.controller.response.ThreadResponse
import cf.youngauthentic.forum.service.RateLimitService
import cf.youngauthentic.forum.service.ThreadService
import cf.youngauthentic.forum.service.exception.AuthException
import cf.youngauthentic.forum.service.exception.NotFoundException
import cf.youngauthentic.forum.service.exception.RateLimitExceededException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping("/thread/{threadId}/reply")
    fun postReply(@RequestHeader headers: Map<String, String>,
                  @PathVariable threadId: String,
                  @RequestBody replyRequest: PostReplyRequest): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            threadService.postReply(headers["token"] ?: "", threadId.toInt(), replyRequest.replyContent)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.warn(e.stackTrace.toString())
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/thread")
    fun postThread(
            @RequestHeader headers: Map<String, String>,
            @RequestBody requestBody: PostThreadRequest
    ): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            threadService.postThread(
                    headers["token"],
                    requestBody.sectionId,
                    requestBody.title,
                    requestBody.content,
                    requestBody.isQuestion
            )
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.warn(e.stackTrace.toString())
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @GetMapping("/thread/{threadId}")
    fun getThread(
            @RequestHeader headers: Map<String, String>,
            @PathVariable("threadId") threadId: String,
            @RequestParam("page", defaultValue = "1") page: String
    ): ResponseEntity<*> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            val thread = threadService.getThread(headers["token"] ?: "", threadId.toInt(), page.toInt())
            responseBody = ThreadResponse(thread)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.warn(e.stackTrace.toString())
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PutMapping("/reply/{replyId}/vote")
    fun updateVote(@PathVariable replyId: String, @RequestHeader headers: Map<String, String>): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            threadService.vote(headers["token"] ?: "", replyId.toInt(), state = 0)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.warn(e.stackTrace.toString())
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PutMapping("/reply/{replyId}")
    fun updateReply(@PathVariable replyId: String,
                    @RequestHeader headers: Map<String, String>,
                    @RequestBody requestBody: UpdateReplyRequest): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            threadService.updateReply(headers["token"] ?: "", replyId.toInt(), requestBody.replyContent)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.warn(e.stackTrace.toString())
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @DeleteMapping("/reply/{replyId}")
    fun deleteReply(@PathVariable replyId: String, @RequestHeader headers: Map<String, String>): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            threadService.deleteReply(headers["token"] ?: "", replyId.toInt())
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.warn(e.stackTrace.toString())
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @DeleteMapping("/thread/{threadId}")
    fun deleteThread(@PathVariable threadId: String, @RequestHeader headers: Map<String, String>): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            threadService.deleteThread(headers["token"] ?: "", threadId.toInt())
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.warn(e.stackTrace.toString())
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

}