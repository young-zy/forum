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

    /**
     * get the thread of the given threadId
     *
     * @param token token of requested user
     * @param threadId the threadId of request
     * @param page page of replies, default value is 1
     * @param size size of each page, default value is 10
     * @throws NotFoundException when thread not found
     * @throws AuthException when user's auth is not enough
     */
    fun getThread(token: String, threadId: Int, page: Int = 1, size: Int = 10): ThreadObject {
        val tokenObj = loginService.getToken(token)
        val threadProjection = threadRepo.findByTid(threadId)
        val thread = ThreadObject(threadProjection)
        val repliesProjection = replyRepo.findAllByTid(threadId,
                PageRequest.of(page - 1, size,
                        Sort.by("priority").descending()))
        val replies = mutableListOf<ReplyObject>()
        var replyObject: ReplyObject
        repliesProjection.forEach {
            replyObject = ReplyObject(it)
            //search for vote info if is a question
            if (thread.isQuestion) {
                if (tokenObj == null) {
                    replyObject.vote = 0
                } else {
                    replyObject.vote = voteRepo.findVoteEntityByUidAndRid(tokenObj.uid, it.rid)?.vote ?: 0
                }
            }
            replies.add(replyObject)
        }
        thread.replies = replies
        return thread
    }

    /**
     * post a new thread
     * @param token token of the operator
     * @param sectionId section of the thread to be posted
     * @param title thread title
     * @param content content of the thread
     * @param isQuestion tell whether this thread is a question
     * @throws AuthException when operator's auth is not enough
     * @throws NotFoundException when section not found
     */
    @Transactional
    @Throws(AuthException::class, NotFoundException::class)
    fun postThread(token: String?, sectionId: Int, title: String, content: String, isQuestion: Boolean) {
        val tokenObj = loginService.getToken(token)
        //TODO hasAuth
        val thread = ThreadEntity(sid = sectionId, title = title, uid = tokenObj!!.uid, question = isQuestion, lastReplyUid = tokenObj.uid)
        threadRepo.saveAndFlush(thread)
        val replyEntity = ReplyEntity(tid = thread.tid, replyContent = content, priority = 9999.9999, uid = thread.uid)
        replyRepo.save(replyEntity)
    }

    /**
     * post a new reply to the thread
     * @param token token of the operator
     * @param threadId section of the thread to be posted
     * @param replyContent thread title
     * @throws AuthException when operator's auth is not enough
     * @throws NotFoundException when section not found
     */
    @Transactional
    @Throws(AuthException::class, NotFoundException::class)
    fun postReply(token: String, threadId: Int, replyContent: String) {
        val tokenObj = loginService.getToken(token)
        if (!threadRepo.existsById(threadId)) {
            throw NotFoundException("thread not found")
        }
        // TODO hasAuth
        val replyEntity = ReplyEntity(tid = threadId, replyContent = replyContent, uid = tokenObj!!.uid)
        replyRepo.save(replyEntity)
    }
}