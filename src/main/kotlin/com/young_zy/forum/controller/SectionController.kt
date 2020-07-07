package com.young_zy.forum.controller

import com.young_zy.forum.controller.request.AddSectionRequest
import com.young_zy.forum.controller.response.Response
import com.young_zy.forum.controller.response.SectionListResponse
import com.young_zy.forum.controller.response.SectionResponse
import com.young_zy.forum.service.RateLimitService
import com.young_zy.forum.service.SectionService
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

    @GetMapping("/section/{sectionId}")
    suspend fun getSection(@PathVariable sectionId: Long,
                           @RequestHeader headers: Map<String, String>,
                           @RequestParam page: Long?,
                           @RequestParam size: Long?): ResponseEntity<Response> {
        val status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        val responseBody = SectionResponse(
                sectionService.getSection(
                        headers["token"] ?: "",
                        sectionId, page ?: 1, size ?: 10
                )
        )
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @GetMapping("/section")
    suspend fun getSections(@RequestHeader headers: Map<String, String>): ResponseEntity<Response> {
        val status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        val responseBody = SectionListResponse(sectionService.getSectionList(headers["token"] ?: ""))
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @PostMapping("/section")
    suspend fun addSection(@RequestHeader headers: Map<String, String>, @RequestBody requestBody: AddSectionRequest): ResponseEntity<Response> {
        val responseBody: Response? = null
        val status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        sectionService.addSection(headers["token"] ?: "", requestBody.sectionName)
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }

    @DeleteMapping("/section/{sectionId}")
    suspend fun deleteSection(@RequestHeader headers: Map<String, String>, @PathVariable sectionId: Long): ResponseEntity<Response> {
        val status = HttpStatus.OK
        val responseHeaders = HttpHeaders()
        rateLimitService.buildHeader(headers, responseHeaders)
        sectionService.deleteSection(headers["token"] ?: "", sectionId)
        val responseBody = Response()
        return ResponseEntity
                .status(status)
                .headers(responseHeaders)
                .body(responseBody)
    }
}