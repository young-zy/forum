package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.model.reply.ReplyEntity
import cf.youngauthentic.forum.model.reply.ReplyObject
import cf.youngauthentic.forum.model.thread.ThreadEntity
import cf.youngauthentic.forum.model.thread.ThreadObject
import cf.youngauthentic.forum.model.vote.VoteEntity
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
import java.sql.Timestamp

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

    @Autowired
    lateinit var authService: AuthService

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
    @Throws(NotFoundException::class, AuthException::class)
    fun getThread(token: String, threadId: Int, page: Int = 1, size: Int = 10): ThreadObject {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        val threadProjection = threadRepo.findByTid(threadId) ?: throw NotFoundException("thread $threadId not found")
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
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
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
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        val replyEntity = ReplyEntity(tid = threadId, replyContent = replyContent, uid = tokenObj!!.uid)
        replyRepo.save(replyEntity)
    }

    /**
     *  delete a thread and all of it's replies
     *  @author young-zy
     *  @param token token of the operator
     *  @param threadId thread to be delete
     *  @throws NotFoundException when thread not found
     *  @throws AuthException when operator's auth is not enough
     */
    @Transactional
    @Throws(AuthException::class, NotFoundException::class)
    fun deleteThread(token: String, threadId: Int) {
        val tokenObj = loginService.getToken(token)
        val thread = threadRepo.findThreadEntityByTid(threadId) ?: throw NotFoundException("thread $threadId not found")
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SECTION_ADMIN,
                allowAuthor = true,
                allowOnlyAuthor = false,
                sectionId = thread.sid))
        threadRepo.delete(thread)
        replyRepo.deleteAllByTid(threadId)
    }


    /**
     * update the content of the reply
     * @author young-zy
     * @param token token of the operator
     * @param replyId reply id of the reply tobe updated
     * @param replyContent the content of the new reply
     * @throws NotFoundException when reply is not found
     * @throws AuthException when operator's auth is not enough
     */
    @Transactional
    @Throws(NotFoundException::class, AuthException::class)
    fun updateReply(token: String, replyId: Int, replyContent: String) {
        val tokenObj = loginService.getToken(token)
        val replyEntity: ReplyEntity = replyRepo.findByRid(replyId) ?: throw NotFoundException("reply not found")
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN,
                allowAuthor = true,
                allowOnlyAuthor = true,
                authorUid = replyEntity.uid))
        replyEntity.replyContent = replyContent
        replyEntity.lastEditTime = Timestamp(System.currentTimeMillis())
        replyRepo.save(replyEntity)
    }

    /**
     * @author young-zy
     * @param token token of the operator
     * @param replyId reply tobe deleted
     * @throws NotFoundException when reply not found
     * @throws AuthException when operator's auth is not enough
     */
    @Transactional
    @Throws(NotFoundException::class, AuthException::class)
    fun deleteReply(token: String, replyId: Int) {
        val tokenObj = loginService.getToken(token)
        val replyEntity: ReplyEntity = replyRepo.findByRid(replyId)
                ?: throw NotFoundException("reply $replyId not found")
        val threadEntity = threadRepo.findThreadEntityByTid(replyEntity.tid)
                ?: throw NotFoundException("thread $replyEntity.tid not found")
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SECTION_ADMIN,
                allowAuthor = true,
                allowOnlyAuthor = false,
                authorUid = replyEntity.uid,
                sectionId = threadEntity.sid))
        replyRepo.delete(replyEntity)
    }

    /**
     * set the vote of a reply
     * if the reply is not in a question, nothing will be done
     * @author young-zy
     * @param token token of the operator
     * @param rid reply tobe voted
     * @param state state tobe set to the reply
     */
    @Transactional
    @Throws(AuthException::class, NotFoundException::class, NotAcceptableException::class)
    fun vote(token: String, rid: Int, state: Int) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        if (!(replyRepo.findByRid(rid)?.threadByTid?.question ?: throw NotFoundException("reply $rid not found"))) {
            throw NotAcceptableException("reply $rid is not in a thread that s a question")
        }
        val voteEntity = VoteEntity(tokenObj!!.uid, rid, state)
        voteRepo.save(voteEntity)
    }
}