package com.young_zy.forum.service

import com.young_zy.forum.model.reply.ReplyEntity
import com.young_zy.forum.model.reply.ReplyObject
import com.young_zy.forum.model.thread.SearchResultDTO
import com.young_zy.forum.model.thread.ThreadEntity
import com.young_zy.forum.model.thread.ThreadObject
import com.young_zy.forum.model.vote.VoteEntity
import com.young_zy.forum.repo.ReplyNativeRepository
import com.young_zy.forum.repo.SectionNativeRepository
import com.young_zy.forum.repo.ThreadNativeRepository
import com.young_zy.forum.repo.VoteNativeRepository
import com.young_zy.forum.service.exception.AuthException
import com.young_zy.forum.service.exception.NotAcceptableException
import com.young_zy.forum.service.exception.NotFoundException
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.time.LocalDateTime
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

@Service
class ThreadService {
    @Autowired
    private lateinit var threadNativeRepository: ThreadNativeRepository

    @Autowired
    private lateinit var replyNativeRepository: ReplyNativeRepository

    @Autowired
    private lateinit var loginService: LoginService

    @Autowired
    private lateinit var voteNativeRepository: VoteNativeRepository

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var sectionNativeRepository: SectionNativeRepository

    @Autowired
    private lateinit var hitRateService: HitRateService

    @Autowired
    private lateinit var transactionalOperator: TransactionalOperator

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
    suspend fun getThread(token: String, threadId: Long, page: Int = 1, size: Int = 10): ThreadObject {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        val threadProjection = threadNativeRepository.findByTid(threadId)
                ?: throw NotFoundException("thread $threadId not found")
        if (tokenObj != null) {
            hitRateService.increment(tokenObj.uid, threadProjection.tid)
        }
        return ThreadObject(
                threadProjection,
                replyNativeRepository.findAllByTid(threadId, page, size).toList(),
                page,
                ceil(replyNativeRepository.countByTid(threadId) / size.toDouble()).toInt()
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
    @Throws(AuthException::class, NotFoundException::class)
    suspend fun postThread(token: String?, sectionId: Long, title: String, content: String, isQuestion: Boolean) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        transactionalOperator.executeAndAwait {
            if (!sectionNativeRepository.existsById(sectionId)) {
                throw NotFoundException("section with $sectionId not found")
            }
            val thread = ThreadEntity(sid = sectionId, title = title, uid = tokenObj!!.uid, question = isQuestion, lastReplyUid = tokenObj.uid)
            val tid = threadNativeRepository.insert(thread)
            val replyEntity = ReplyEntity(tid = tid, replyContent = content, priority = 9999.9999, uid = thread.uid)
            replyNativeRepository.insert(replyEntity)
        }
    }

    /**
     * post a new reply to the thread
     * @param token token of the operator
     * @param threadId section of the thread to be posted
     * @param replyContent thread title
     * @throws AuthException when operator's auth is not enough
     * @throws NotFoundException when section not found
     */
    @Throws(AuthException::class, NotFoundException::class)
    suspend fun postReply(token: String, threadId: Long, replyContent: String) {
        val tokenObj = loginService.getToken(token)
        transactionalOperator.executeAndAwait {
            if (!threadNativeRepository.existsById(threadId)) {
                throw NotFoundException("thread not found")
            }
            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
            val replyEntity = ReplyEntity(tid = threadId, replyContent = replyContent, uid = tokenObj!!.uid)
            replyNativeRepository.insert(replyEntity)
        }
    }

    /**
     *  delete a thread and all of it's replies
     *  @author young-zy
     *  @param token token of the operator
     *  @param threadId thread to be delete
     *  @throws NotFoundException when thread not found
     *  @throws AuthException when operator's auth is not enough
     */
    @Throws(AuthException::class, NotFoundException::class)
    suspend fun deleteThread(token: String, threadId: Long) {
        val tokenObj = loginService.getToken(token)
        transactionalOperator.executeAndAwait {
            val thread = threadNativeRepository.findThreadEntityByTid(threadId)
                    ?: throw NotFoundException("thread $threadId not found")
            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SECTION_ADMIN,
                    allowAuthor = true,
                    allowOnlyAuthor = false,
                    sectionId = thread.sid,
                    authorUid = thread.uid))
            replyNativeRepository.deleteAllByTid(threadId)
            threadNativeRepository.delete(thread)
        }
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
    @Throws(NotFoundException::class, AuthException::class)
    suspend fun updateReply(token: String, replyId: Long, replyContent: String) {
        val tokenObj = loginService.getToken(token)
        transactionalOperator.executeAndAwait {
            val replyEntity: ReplyEntity = replyNativeRepository.findReplyEntityByRid(replyId)
                    ?: throw NotFoundException("reply not found")
            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN,
                    allowAuthor = true,
                    allowOnlyAuthor = true,
                    authorUid = replyEntity.uid))
            replyEntity.replyContent = replyContent
            replyEntity.lastEditTime = LocalDateTime.now()
            replyNativeRepository.update(replyEntity)
        }
    }

    /**
     * @author young-zy
     * @param token token of the operator
     * @param replyId reply tobe deleted
     * @throws NotFoundException when reply not found
     * @throws AuthException when operator's auth is not enough
     */
    @Throws(NotFoundException::class, AuthException::class)
    suspend fun deleteReply(token: String, replyId: Long) {
        val tokenObj = loginService.getToken(token)
        transactionalOperator.executeAndAwait {
            val replyEntity = replyNativeRepository.findReplyEntityByRid(replyId)
                    ?: throw NotFoundException("reply $replyId not found")
            val threadEntity = threadNativeRepository.findThreadEntityByTid(replyEntity.tid)
                    ?: throw NotFoundException("thread ${replyEntity.tid} not found")
            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SECTION_ADMIN,
                    allowAuthor = true,
                    allowOnlyAuthor = false,
                    authorUid = replyEntity.uid,
                    sectionId = threadEntity.sid))
            replyNativeRepository.delete(replyEntity)
        }
    }

    /**
     * set the vote of a reply
     * if the reply is not in a question, nothing will be done
     * @author young-zy
     * @param token token of the operator
     * @param rid reply tobe voted
     * @param state state tobe set to the reply
     */
    @Throws(AuthException::class, NotFoundException::class, NotAcceptableException::class)
    suspend fun vote(token: String, rid: Long, state: Long) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        transactionalOperator.executeAndAwait {
            val reply = replyNativeRepository.findReplyEntityByRid(rid)
                    ?: throw NotFoundException("reply $rid not found")
            var voteEntity = voteNativeRepository.findVoteEntityByUidAndRid(tokenObj!!.uid, rid)
            if (voteEntity !== null) {
                if (voteEntity.vote > 0 && state < 0) {     // from upVote to downVote
                    reply.upVote--
                    reply.downVote++
                    reply.priority -= 2
                } else if (voteEntity.vote < 0 && state > 0) {  // from downVote to upVote
                    reply.upVote++
                    reply.downVote--
                    reply.priority += 2
                } else if (voteEntity.vote == state) {      // same

                } else {                                    // no vote
                    if (state > 0) {
                        reply.upVote++
                        reply.priority++
                    } else if (state == 0.toLong()) {
                        if (voteEntity.vote > 0) {
                            reply.upVote--
                            reply.priority--
                        } else {
                            reply.downVote--
                            reply.priority++
                        }
                    } else {
                        reply.downVote++
                        reply.priority--
                    }
                }
                voteEntity.vote = state
            } else {
                voteEntity = VoteEntity(tokenObj.uid, rid, state)
                if (state > 0) {
                    reply.upVote++
                    reply.priority++
                } else if (state < 0) {
                    reply.downVote++
                    reply.priority--
                }
            }
            reply.priority = max(0.0, reply.priority)
            reply.priority = min(9999.0, reply.priority)
            replyNativeRepository.update(reply)
            voteNativeRepository.save(voteEntity)
        }
    }

    @Throws(NotFoundException::class, AuthException::class)
    suspend fun getReply(token: String, replyId: Int): ReplyObject {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        return replyNativeRepository.findByRid(replyId, tokenObj!!.uid)
                ?: throw NotFoundException("reply of rid $replyId not found")
    }

    /**
     *  search the keyword among all the thread titles
     *  @author young-zy
     *  @param token token of user
     *  @param keyWord keyword about to be searched
     *  @param page pageof the result
     */
    @Throws(AuthException::class)
    suspend fun search(token: String, keyWord: String, page: Int, size: Int): List<SearchResultDTO> {
        return threadNativeRepository.searchInTitle(keyWord, page, size).toList()
    }

    @Throws(NotFoundException::class, AuthException::class)
    suspend fun setBestAnswer(token: String, threadId: Long, replyId: Long) {
        val tokenObj = loginService.getToken(token)
        transactionalOperator.executeAndAwait {
            val thread = threadNativeRepository.findThreadEntityByTid(threadId)
                    ?: throw NotFoundException("thread with threadId $threadId not found")
            val reply = replyNativeRepository.findReplyEntityByRid(replyId)
                    ?: throw NotFoundException("reply with replyId $replyId not found")
            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER, allowAuthor = true, allowOnlyAuthor = true, authorUid = thread.uid, sectionId = thread.sid))
            if (thread.hasBestAnswer) {
                throw NotAcceptableException("thread with threadId $threadId already has best answer")
            }
            thread.hasBestAnswer = true
            reply.bestAnswer = true
            threadNativeRepository.update(thread)
            replyNativeRepository.update(reply)
        }
    }
}