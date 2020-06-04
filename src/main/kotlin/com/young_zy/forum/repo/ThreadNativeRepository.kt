package com.young_zy.forum.repo

import com.young_zy.forum.config.toBoolean
import com.young_zy.forum.model.thread.SearchResultDTO
import com.young_zy.forum.model.thread.ThreadEntity
import com.young_zy.forum.model.thread.ThreadInListProjection
import com.young_zy.forum.model.thread.ThreadProjection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.r2dbc.core.awaitRowsUpdated
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.stereotype.Repository
import java.math.BigInteger
import java.time.LocalDateTime

@Repository
class ThreadNativeRepository {
    @Autowired
    private lateinit var r2dbcDatabaseClient: DatabaseClient

    suspend fun findAllBySid(sid: Int, page: Int, size: Int): Flow<ThreadInListProjection> {
//        return r2dbcDatabaseClient.select()
//                .from("thread")
//                .orderBy(Sort.by("lastReplyTime").descending())
//                .page(PageRequest.of(page, size))
//                .`as`(ThreadInListProjection::class.java)
//                .fetch()
//                .all()
//                .asFlow()
        return r2dbcDatabaseClient.execute("select tid, title, lastReplyTime, hasBestAnswer, postTime, question, uid, username from thread natural join user where sid=:sid")
                .bind("sid", sid)
                .map { t ->
                    ThreadInListProjection(
                            t["tid"] as Int,
                            t["title"] as String,
                            t["lastReplyTime"] as LocalDateTime,
                            t["postTime"] as LocalDateTime,
                            t["uid"] as Long,
                            t["username"] as String,
                            (t["question"] as Byte).toBoolean(),
                            t["hasBestAnswer"] as Boolean
                    )
                }
                .all()
                .asFlow()
    }

    suspend fun findByTid(tid: Int): ThreadProjection? {
        return r2dbcDatabaseClient.execute("select tid, title, lastReplyTime, postTime, question, hasBestAnswer, u.uid , u.username from thread left join user u on thread.uid = u.uid where tid=:tid")
                .bind("tid", tid)
                .map { t ->
                    ThreadProjection(
                            (t["tid"] as BigInteger).toInt(),
                            t["title"] as String,
                            t["lastReplyTime"] as LocalDateTime,
                            t["postTime"] as LocalDateTime,
                            (t["question"] as Byte).toBoolean(),
                            (t["hasBestAnswer"] as Byte).toBoolean(),
                            t["uid"] as Long,
                            t["username"] as String
                    )
                }
                .one()
                .awaitFirstOrNull()
    }

    suspend fun countBySid(sid: Int): Long {
        return r2dbcDatabaseClient.execute("select count(*) as count from thread where sid=:sid")
                .bind("sid", sid)
                .map { t ->
                    t["count"] as Long
                }
                .awaitOne()
    }

    suspend fun searchInTitle(keyword: String, page: Int, amount: Int): Flow<SearchResultDTO> {
        return r2dbcDatabaseClient.execute("select tid,title,lastReplyTime,postTime,uid,username,question,hasBestAnswer from (select * from  thread  where  match  (title)  against  (:keyword IN NATURAL LANGUAGE MODE) ORDER BY lastReplyTime) as t natural join user limit :offset,:amount")
                .bind("keyword", keyword)
                .bind("offset", (page - 1) * amount)
                .bind("amount", amount)
                .fetch()
                .all()
                .map { t ->
                    SearchResultDTO(
                            t["tid"] as BigInteger,
                            t["title"] as String,
                            t["lastReplyTime"] as LocalDateTime,
                            t["postTime"] as LocalDateTime,
                            t["uid"] as Long,
                            t["username"] as String,
                            (t["question"] as Byte).toBoolean(),
                            (t["hasBestAnswer"] as Byte).toBoolean()
                    )
                }
                .asFlow()
    }

    suspend fun findThreadEntityByTid(tid: Int): ThreadEntity? {
        return r2dbcDatabaseClient.select()
                .from(ThreadEntity::class.java)
                .matching(where("tid").`is`(tid))
                .fetch()
                .awaitOneOrNull()
    }

    suspend fun insert(thread: ThreadEntity): Int {
        return r2dbcDatabaseClient.insert()
                .into(ThreadEntity::class.java)
                .using(thread)
                .map { t ->
                    t["tid"] as Int
                }
                .one()
                .awaitSingle()
    }

    suspend fun existsById(threadId: Int): Boolean {
        return r2dbcDatabaseClient.execute("select count(*) as count from thread where tid = :tid")
                .bind("tid", threadId)
                .map { t ->
                    t["count"] as Int > 0
                }
                .awaitOne()
    }

    suspend fun delete(thread: ThreadEntity): Int {
        return r2dbcDatabaseClient.delete()
                .from(ThreadEntity::class.java)
                .matching(where("tid").`is`(thread.tid!!))
                .fetch()
                .awaitRowsUpdated()
    }
}