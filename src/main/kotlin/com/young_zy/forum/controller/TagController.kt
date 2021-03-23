package com.young_zy.forum.controller

import com.young_zy.forum.model.thread.ThreadNode
import com.young_zy.forum.service.TagService
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController {

    @Autowired
    private lateinit var tagService: TagService

    @GetMapping("/tag/{tagId}")
    suspend fun getThreadsByTag(
        @PathVariable tagId: Long,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): List<ThreadNode> {
        return tagService.getThreads(tagId, page ?: 1, size ?: 10).toList()
    }

}