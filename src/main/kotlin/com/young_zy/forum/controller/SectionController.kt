package com.young_zy.forum.controller

import com.young_zy.forum.controller.request.AddSectionRequest
import com.young_zy.forum.controller.response.Response
import com.young_zy.forum.controller.response.SectionListResponse
import com.young_zy.forum.controller.response.SectionResponse
import com.young_zy.forum.service.RateLimitService
import com.young_zy.forum.service.SectionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
class SectionController {

    @Autowired
    private lateinit var sectionService: SectionService

    @Autowired
    private lateinit var rateLimitService: RateLimitService

    @GetMapping("/section/{sectionId}")
    suspend fun getSection(
        @PathVariable sectionId: Long,
        @RequestParam page: Long?,
        @RequestParam size: Long?
    ): Response? {
        rateLimitService.buildHeader()
        val responseBody = SectionResponse(
            sectionService.getSection(
                sectionId, page ?: 1, size ?: 10
            )
        )
        return responseBody
    }

    @GetMapping("/section")
    suspend fun getSections(): Response? {
        rateLimitService.buildHeader()
        val responseBody = SectionListResponse(sectionService.getSectionList())
        return responseBody
    }

    @PostMapping("/section")
    suspend fun addSection(@RequestBody requestBody: AddSectionRequest): Response? {
        val responseBody: Response? = null
        rateLimitService.buildHeader()
        sectionService.addSection(requestBody.sectionName)
        return responseBody
    }

    @DeleteMapping("/section/{sectionId}")
    suspend fun deleteSection(@PathVariable sectionId: Long): Response? {
        rateLimitService.buildHeader()
        return Response()
    }
}