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
import com.young_zy.forum.common.exception.ForbiddenException
import com.young_zy.forum.common.exception.ConflictException
import com.young_zy.forum.common.exception.NotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.casbin.jcasbin.main.Enforcer
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
    private lateinit var enforcer: Enforcer

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

    @Autowired
    private lateinit var tagService: TagService

    /**
     * get the thread of the given threadId
     *
     * @param threadId the threadId of request
     * @param page page of replies, default value is 1
     * @param size size of each page, default value is 10
     * @throws NotFoundException when thread not found
     * @throws ForbiddenException when user's auth is not enough
     */
    @Throws(NotFoundException::class, ForbiddenException::class, ConflictException::class)
    suspend fun getThread(threadId: Long, page: Int = 1, size: Int = 10, orderBy: String): ThreadObject {
        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        val orderList = setOf("postTime", "priority")
        if (orderBy !in orderList) {
            throw ConflictException("parameter \"orderBy\" not acceptable")
        }
        return withContext(Dispatchers.IO) {
            val thread = async {
                threadNativeRepository.findByTid(threadId).awaitSingleOrNull()
                    ?: throw NotFoundException("thread $threadId not found")
            }
            val replies = async {
                replyNativeRepository.findAllByTid(threadId, tokenObj?.uid ?: -1, page, size).toList()
            }
            val tags = tagService.getTags(threadId)
            if (tokenObj != null) {
                hitRateService.increment(tokenObj.uid, threadId)
            }
            val t = thread.await()
            ThreadObject(
                t,
                replies.await(),
                page,
                ceil(replyNativeRepository.countByTid(threadId).awaitSingle() / size.toDouble()).toInt()
                    .coerceAtLeast(1),
                null,
                tags.toList()
            )
        }
    }

    /**
     * post a new thread
     * @param sectionId section of the thread to be posted
     * @param title thread title
     * @param content content of the thread
     * @param isQuestion tell whether this thread is a question
     * @throws ForbiddenException when operator's auth is not enough
     * @throws NotFoundException when section not found
     */
    @Throws(ForbiddenException::class, NotFoundException::class)
    suspend fun postThread(sectionId: Long, title: String, content: String, isQuestion: Boolean, tags: List<String>) {
        val tokenObj = loginService.getToken()
        if (!enforcer.enforce(tokenObj, "", "postThread")) {
            throw ForbiddenException("permission denied")
        }
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        transactionalOperator.executeAndAwait {
            if (!sectionNativeRepository.existsById(sectionId).awaitSingle()) {
                throw NotFoundException("section with $sectionId not found")
            }
            var thread = ThreadEntity(
                sid = sectionId,
                title = title,
                threadContent = content,
                uid = tokenObj!!.uid,
                question = isQuestion,
                lastReplyUid = tokenObj.uid
            )
            thread = threadNativeRepository.insert(thread).awaitSingle()
            tagService.addRelation(thread, tags)
            //            val inserted = threadNativeRepository.insert(thread)
//            val replyEntity = ReplyEntity(tid = inserted.awaitSingle().tid!!, replyContent = content, priority = 9999.9999, uid = thread.uid)
//            replyNativeRepository.insert(replyEntity)
        }
    }

    /**
     * post a new reply to the thread
     * @param threadId section of the thread to be posted
     * @param replyContent thread title
     * @throws ForbiddenException when operator's auth is not enough
     * @throws NotFoundException when section not found
     */
    @Throws(ForbiddenException::class, NotFoundException::class)
    suspend fun postReply(threadId: Long, replyContent: String) {
        val tokenObj = loginService.getToken()
        transactionalOperator.executeAndAwait {
            val thread = threadNativeRepository.findByTid(threadId).awaitSingleOrNull()
                ?: throw NotFoundException("thread not found")
//            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
            if (!enforcer.enforce(tokenObj, "", "postReply")) {
                throw ForbiddenException("permission denied")
            }
            val replyEntity =
                ReplyEntity(tid = threadId, sid = thread.sid, replyContent = replyContent, uid = tokenObj!!.uid)
            replyNativeRepository.insert(replyEntity).awaitSingle()
        }
    }

    /**
     *  delete a thread and all of it's replies
     *  @author young-zy
     *  @param threadId thread to be delete
     *  @throws NotFoundException when thread not found
     *  @throws ForbiddenException when operator's auth is not enough
     */
    @Throws(ForbiddenException::class, NotFoundException::class)
    suspend fun deleteThread(threadId: Long) {
        val tokenObj = loginService.getToken()
        transactionalOperator.executeAndAwait {
            val thread = threadNativeRepository.findThreadEntityByTid(threadId).awaitSingleOrNull()
                ?: throw NotFoundException("thread $threadId not found")
//            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SECTION_ADMIN,
//                    allowAuthor = true,
//                    allowOnlyAuthor = false,
//                    sectionId = thread.sid,
//                    authorUid = thread.uid))
            if (!enforcer.enforce(tokenObj, thread, "deleteThread")) {
                throw ForbiddenException("permission denied")
            }
            replyNativeRepository.deleteAllByTid(threadId)
            threadNativeRepository.delete(thread).awaitSingle()
        }
    }


    /**
     * update the content of the reply
     * @author young-zy
     * @param replyId reply id of the reply tobe updated
     * @param replyContent the content of the new reply
     * @throws NotFoundException when reply is not found
     * @throws ForbiddenException when operator's auth is not enough
     */
    @Throws(NotFoundException::class, ForbiddenException::class)
    suspend fun updateReply(replyId: Long, replyContent: String) {
        val tokenObj = loginService.getToken()
        transactionalOperator.executeAndAwait {
            val replyEntity: ReplyEntity = replyNativeRepository.findReplyEntityByRid(replyId).awaitSingleOrNull()
                ?: throw NotFoundException("reply not found")
//            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN,
//                    allowAuthor = true,
//                    allowOnlyAuthor = true,
//                    authorUid = replyEntity.uid))
            if (!enforcer.enforce(tokenObj, replyEntity, "updateReply")) {
                throw ForbiddenException("permission denied")
            }
            replyEntity.replyContent = replyContent
            replyEntity.lastEditTime = LocalDateTime.now()
            replyNativeRepository.update(replyEntity).awaitSingle()
        }
    }

    /**
     * @author young-zy
     * @param replyId reply tobe deleted
     * @throws NotFoundException when reply not found
     * @throws ForbiddenException when operator's auth is not enough
     */
    @Throws(NotFoundException::class, ForbiddenException::class)
    suspend fun deleteReply(replyId: Long) {
        val tokenObj = loginService.getToken()
        transactionalOperator.executeAndAwait {
            val replyEntity = replyNativeRepository.findReplyEntityByRid(replyId).awaitSingleOrNull()
                ?: throw NotFoundException("reply $replyId not found")
            val threadEntity = threadNativeRepository.findThreadEntityByTid(replyEntity.tid).awaitSingleOrNull()
                ?: throw NotFoundException("thread ${replyEntity.tid} not found")
//            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SECTION_ADMIN,
//                    allowAuthor = true,
//                    allowOnlyAuthor = false,
//                    authorUid = replyEntity.uid,
//                    sectionId = threadEntity.sid))
            if (!enforcer.enforce(tokenObj, replyEntity, "deleteReply")) {
                throw ForbiddenException("permission denied")
            }
            replyNativeRepository.delete(replyEntity).awaitSingle()
        }
    }

    /**
     * set the vote of a reply
     * if the reply is not in a question, nothing will be done
     * @author young-zy
     * @param rid reply tobe voted
     * @param state state tobe set to the reply
     */
    @Throws(ForbiddenException::class, NotFoundException::class, ConflictException::class)
    suspend fun vote(rid: Long, state: Long) {
        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        if (!enforcer.enforce(tokenObj, "", "vote")) {
            throw ForbiddenException("permission denied")
        }
        transactionalOperator.executeAndAwait {
            val reply = replyNativeRepository.findReplyEntityByRid(rid).awaitSingleOrNull()
                ?: throw NotFoundException("reply $rid not found")
            var voteEntity = voteNativeRepository.findVoteEntityByUidAndRid(tokenObj!!.uid, rid).awaitSingleOrNull()
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
                            if (reply.priority >= 0.000001) {
                                reply.priority++
                            }
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
            replyNativeRepository.update(reply).awaitSingle()
            voteNativeRepository.save(voteEntity).awaitSingle()
        }
    }

    @Throws(NotFoundException::class, ForbiddenException::class)
    suspend fun getReply(replyId: Int): ReplyObject {
        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        return replyNativeRepository.findByRid(replyId, tokenObj?.uid ?: -1).awaitSingleOrNull()
            ?: throw NotFoundException("reply of rid $replyId not found")
    }

    /**
     *  search the keyword among all the thread titles
     *  @author young-zy
     *  @param keyWord keyword about to be searched
     *  @param page page of the result
     */
    @Throws(ForbiddenException::class)
    suspend fun search(keyWord: String, page: Long, size: Long): List<SearchResultDTO> {
        return threadNativeRepository.searchInTitle(keyWord, page, size).toList()
    }

    @Throws(NotFoundException::class, ForbiddenException::class)
    suspend fun setBestAnswer(replyId: Long) {
        val tokenObj = loginService.getToken()
        transactionalOperator.executeAndAwait {
            val reply = replyNativeRepository.findReplyEntityByRid(replyId).awaitSingleOrNull()
                ?: throw NotFoundException("reply with replyId $replyId not found")
            val threadId = reply.tid
            val thread = threadNativeRepository.findThreadEntityByTid(threadId).awaitSingleOrNull()
                ?: throw NotFoundException("thread with threadId $threadId not found")
//            authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER,
//                allowAuthor = true,
//                allowOnlyAuthor = true,
//                authorUid = thread.uid,
//                sectionId = thread.sid
//            ))
            if (!enforcer.enforce(tokenObj, thread)) {
                throw ForbiddenException("permission denied")
            }
            if (thread.bestAnswer != null) {
                throw ConflictException("thread with threadId $threadId already has best answer")
            }
            reply.bestAnswer = true
            thread.bestAnswer = replyId
            threadNativeRepository.update(thread).awaitSingle()
            replyNativeRepository.update(reply).awaitSingle()
        }
    }
}