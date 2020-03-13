package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.model.reply.ReplyEntity
import cf.youngauthentic.forum.model.reply.ReplyObject
import cf.youngauthentic.forum.model.thread.ThreadEntity
import cf.youngauthentic.forum.model.thread.ThreadObject
import cf.youngauthentic.forum.repo.ReplyRepository
import cf.youngauthentic.forum.repo.ThreadRepository
import cf.youngauthentic.forum.repo.VoteRepository
import cf.youngauthentic.forum.service.exception.AuthException
import cf.youngauthentic.forum.service.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ThreadService {
    @Autowired
    lateinit var threadRepo: ThreadRepository

    @Autowired
    lateinit var replyRepo: ReplyRepository

    @Autowired
    lateinit var loginService: LoginService

    @Autowired
    lateinit var voteRepo: VoteRepository

    fun getThread(token: String, threadId: Int, page: Int = 1, size: Int = 10): ThreadObject {
        val tokenObj = loginService.getToken(token)
        val threadProjection = threadRepo.findByTid(threadId)
        val thread = ThreadObject(threadProjection)
        val repliesProjection = replyRepo.findAllByTid(threadId,
                PageRequest.of(page, size,
                        Sort.by("priority").descending()))
        val replies = mutableListOf<ReplyObject>()
        var replyObject: ReplyObject
        repliesProjection.forEach {
            replyObject = ReplyObject(it)
            //search for vote info if is a question
            if (thread.isQuestion) {
                replyObject.vote = voteRepo.findVoteEntityByUidAndRid(tokenObj!!.uid, it.rid).vote
            }
            replies.add(replyObject)
        }
        thread.replies = replies
        return thread
    }

    @Transactional
    @Throws(AuthException::class)
    fun postThread(token: String?, sectionId: Int, title: String, content: String, isQuestion: Boolean) {
        val tokenObj = loginService.getToken(token)
        //TODO hasAuth
        val thread = ThreadEntity(sid = sectionId, title = title, uid = tokenObj!!.uid, question = isQuestion)
        threadRepo.saveAndFlush(thread)
        val replyEntity = ReplyEntity(tid = thread.tid, replyContent = content, priority = 99999.99999)
        replyRepo.save(replyEntity)
    }

    @Transactional
    @Throws(AuthException::class, NotFoundException::class)
    fun postReply(token: String, threadId: Int, replyContent: String) {
        if (!threadRepo.existsById(threadId)) {
            throw NotFoundException()
        }
        // TODO hasAuth
        val replyEntity = ReplyEntity(tid = threadId, replyContent = replyContent)
        replyRepo.save(replyEntity)
    }
}