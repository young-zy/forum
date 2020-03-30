package cf.youngauthentic.forum.controller

import cf.youngauthentic.forum.config.stackTraceString
import cf.youngauthentic.forum.controller.request.AddSectionRequest
import cf.youngauthentic.forum.controller.response.Response
import cf.youngauthentic.forum.controller.response.SectionListResponse
import cf.youngauthentic.forum.controller.response.SectionResponse
import cf.youngauthentic.forum.service.RateLimitService
import cf.youngauthentic.forum.service.SectionService
import cf.youngauthentic.forum.service.exception.AuthException
import cf.youngauthentic.forum.service.exception.NotFoundException
import cf.youngauthentic.forum.service.exception.RateLimitExceededException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
class SectionController {

    @Autowired
    private lateinit var sectionService: SectionService

    @Autowired
    private lateinit var rateLimitService: RateLimitService

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/section/{sectionId}")
    fun getSection(@PathVariable sectionId: Int,
                   @RequestHeader headers: Map<String, String>,
                   @RequestParam page: Int,
                   @RequestParam size: Int): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            responseBody = SectionResponse(sectionService.getSection(headers["token"] ?: "", sectionId, page, size))
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.error(e.stackTraceString)
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @GetMapping("/section")
    fun getSections(@RequestHeader headers: Map<String, String>): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            responseBody = SectionListResponse(sectionService.getSectionList(headers["token"] ?: ""))
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.error(e.stackTraceString)
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @PostMapping("/section")
    fun addSection(@RequestHeader headers: Map<String, String>, @RequestBody requestBody: AddSectionRequest): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            sectionService.addSection(headers["token"] ?: "", requestBody.sectionName)
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.error(e.stackTraceString)
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }

    @DeleteMapping("/section/{sectionId}")
    fun deleteSection(@RequestHeader headers: Map<String, String>, @PathVariable sectionId: Int): ResponseEntity<Response> {
        var responseBody: Response? = null
        var status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        try {
            rateLimitService.buildHeader(headers, responseHeaders)
            sectionService.deleteSection(headers["token"] ?: "", sectionId)
            responseBody = Response()
        } catch (e: RateLimitExceededException) {
            status = HttpStatus.TOO_MANY_REQUESTS
            responseBody = Response(false, e.message)
        } catch (e: NotFoundException) {
            status = HttpStatus.NOT_FOUND
            responseBody = Response(false, e.message)
        } catch (e: AuthException) {
            status = HttpStatus.UNAUTHORIZED
            responseBody = Response(false, e.message)
        } catch (e: Exception) {
            status = HttpStatus.INTERNAL_SERVER_ERROR
            logger.error(e.stackTraceString)
            responseBody = Response(false, e.message ?: "")
        } finally {
            return ResponseEntity
                    .status(status)
                    .headers(responseHeaders)
                    .body(responseBody)
        }
    }
}