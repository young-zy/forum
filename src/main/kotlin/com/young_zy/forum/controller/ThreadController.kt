package com.young_zy.forum.controller

import com.young_zy.forum.controller.request.PostThreadRequest
import com.young_zy.forum.controller.request.ReplyRequest
import com.young_zy.forum.controller.response.*
import com.young_zy.forum.repo.ThreadNativeRepository
import com.young_zy.forum.service.HitRateService
import com.young_zy.forum.service.RateLimitService
import com.young_zy.forum.service.TagService
import com.young_zy.forum.service.ThreadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@CrossOrigin
@RestController
class ThreadController {
    @Autowired
    lateinit var rateLimitService: RateLimitService

    @Autowired
    lateinit var threadService: ThreadService

    @Autowired
    lateinit var hitRateService: HitRateService

    @Autowired
    lateinit var tagService: TagService

    @Autowired
    private lateinit var threadNativeRepository: ThreadNativeRepository

    @PostMapping("/thread/{threadId}/reply")
    suspend fun postReply(
        @PathVariable threadId: Long,
        @RequestBody replyRequest: ReplyRequest
    ): Response? {
        var responseBody: Response? = null
        rateLimitService.buildHeader()
        threadService.postReply(threadId, replyRequest.replyContent)
        return responseBody
    }

    @PostMapping("/thread")
    suspend fun postThread(
        @RequestBody requestBody: PostThreadRequest
    ): Response? {
        val responseBody: Response? = null
        rateLimitService.buildHeader()
        threadService.postThread(
            requestBody.sectionId,
            requestBody.title,
            requestBody.content,
            requestBody.isQuestion,
            requestBody.tags
        )
        return responseBody
    }

    @GetMapping("/thread/{threadId}")
    suspend fun getThread(
        @PathVariable("threadId") threadId: Long,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?,
        @RequestParam("orderBy") orderBy: String?
    ): Response? {
        var responseBody: Response?
        rateLimitService.buildHeader()
        val thread = threadService.getThread(
            threadId, page ?: 1, size ?: 10, orderBy
                ?: "priority"
        )
        responseBody = ThreadResponse(thread)
        return responseBody
    }

    @PutMapping("/reply/{replyId}/vote")
    suspend fun updateVote(@PathVariable replyId: Long, @RequestParam state: Long): Response? {
        val responseBody: Response? = null
        rateLimitService.buildHeader()
        threadService.vote(replyId, state)
        return responseBody
    }

    @PutMapping("/reply/{replyId}")
    suspend fun updateReply(
        @PathVariable replyId: Long,
        @RequestBody requestBody: ReplyRequest
    ): Response? {
        var responseBody: Response? = null
        rateLimitService.buildHeader()
        threadService.updateReply(replyId, requestBody.replyContent)
        return responseBody
    }

    @DeleteMapping("/reply/{replyId}")
    suspend fun deleteReply(@PathVariable replyId: Long): Response? {
        var responseBody: Response? = null
        rateLimitService.buildHeader()
        threadService.deleteReply(replyId)
        return responseBody
    }

    @DeleteMapping("/thread/{threadId}")
    suspend fun deleteThread(@PathVariable threadId: Long): Response? {
        var responseBody: Response? = null
        rateLimitService.buildHeader()
        threadService.deleteThread(threadId)
        return responseBody
    }

    @GetMapping("/reply/{replyId}")
    suspend fun getReply(@PathVariable replyId: Int): Response? {
        var responseBody: Response?
        rateLimitService.buildHeader()
        responseBody = ReplyResponse(threadService.getReply(replyId))
        return responseBody
    }

    @GetMapping("/search")
    suspend fun search(
        @RequestParam keyWord: String,
        @RequestParam size: Long?, @RequestParam page: Long?
    ): Response? {
        var responseBody: Response?
        rateLimitService.buildHeader()
        responseBody = SearchResponse(threadService.search(keyWord, page ?: 1, size ?: 10))
        return responseBody
    }

    @GetMapping("/thread/hot")
    suspend fun getHotThreads(@RequestParam count: Int?): Response? {
        var responseBody: Response?
        rateLimitService.buildHeader()
        responseBody = HitRateResponse(hitRateService.getOrder(count ?: 20))
        return responseBody
    }

    @PutMapping("/thread/{threadId}/tag")
    suspend fun updateTag(@PathVariable threadId: Long, @RequestBody tags: List<String>) {
        tagService.updateRelation(threadId, tags)
    }
}