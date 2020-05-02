package com.young_zy.forum.service

import com.young_zy.forum.model.SearchObject
import com.young_zy.forum.model.reply.ReplyEntity
import com.young_zy.forum.model.reply.ReplyObject
import com.young_zy.forum.model.thread.ThreadEntity
import com.young_zy.forum.model.thread.ThreadObject
import com.young_zy.forum.model.vote.VoteEntity
import com.young_zy.forum.repo.ReplyRepository
import com.young_zy.forum.repo.ThreadRepository
import com.young_zy.forum.repo.VoteRepository
import com.young_zy.forum.service.exception.AuthException
import com.young_zy.forum.service.exception.NotAcceptableException
import com.young_zy.forum.service.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import kotlin.math.ceil

@Service
class ThreadService {
    @Autowired
    private lateinit var threadRepo: ThreadRepository

    @Autowired
    private lateinit var replyRepo: ReplyRepository

    @Autowired
    private lateinit var loginService: LoginService

    @Autowired
    private lateinit var voteRepo: VoteRepository

    @Autowired
    private lateinit var authService: AuthService

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
        val repliesProjection = replyRepo.findAllByTid(threadId,
                PageRequest.of(page - 1, size,
                        Sort.by("priority").descending()))
        val replies = mutableListOf<ReplyObject>()
        var replyObject: ReplyObject
        repliesProjection.forEach {
            replyObject = ReplyObject(it)
            //search for vote info if is a question
            if (threadProjection.question) {
                if (tokenObj == null) {
                    replyObject.vote = 0
                } else {
                    replyObject.vote = voteRepo.findVoteEntityByUidAndRid(tokenObj.uid, it.rid)?.vote ?: 0
                }
            }
            replies.add(replyObject)
        }
        return ThreadObject(
                threadProjection,
                replies,
                page,
                ceil(replyRepo.countByTid(threadId) / size.toDouble()).toInt()
        )
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
                sectionId = thread.sid,
                authorUid = thread.uid))
        replyRepo.deleteAllByTid(threadId)
        threadRepo.delete(thread)
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
        val reply = replyRepo.findByRid(rid)
        if (!(reply?.threadByTid?.question ?: throw NotFoundException("reply $rid not found"))) {
            throw NotAcceptableException("reply $rid is not in a thread that is a question")
        }
        var voteEntity = voteRepo.findVoteEntityByUidAndRid(tokenObj!!.uid, rid)
        if (voteEntity !== null) {
            if (voteEntity.vote > 0 && state < 0) {
                reply.upVote--
                reply.downVote++
            } else if (voteEntity.vote < 0 && state > 0) {
                reply.upVote++
                reply.downVote--
            } else if (voteEntity.vote == state) {      //same

            } else {
                if (state > 0) {
                    reply.upVote++
                } else if (state == 0) {
                    if (voteEntity.vote > 0) {
                        reply.upVote--
                    } else {
                        reply.downVote--
                    }
                } else {
                    reply.downVote++
                }
            }
        } else {
            voteEntity = VoteEntity(tokenObj.uid, rid, state)
            if (state > 0) {
                reply.upVote++
            } else if (state < 0) {
                reply.downVote++
            }
        }
        replyRepo.save(reply)
        voteRepo.save(voteEntity)
    }

    fun search(token: String, keyWord: String, page: Int, size: Int): SearchObject {
        return SearchObject(threadRepo.searchInTitle(keyWord, PageRequest.of(page - 1, size)), page, 0)
    }
}