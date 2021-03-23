package com.young_zy.forum.repo

import com.young_zy.forum.config.toBoolean
import com.young_zy.forum.model.thread.SearchResultDTO
import com.young_zy.forum.model.thread.ThreadEntity
import com.young_zy.forum.model.thread.ThreadInListProjection
import com.young_zy.forum.model.thread.ThreadProjection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.*
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class ThreadNativeRepository {

    @Autowired
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    suspend fun findAllBySid(sid: Long, page: Long, size: Long): Flow<ThreadInListProjection> {
        val sql =
            "select tid, title, threadContent, lastReplyTime, bestAnswer, postTime, question, uid, username from thread natural join user where sid=:sid order by tid desc limit :offset,:amount"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("sid", sid)
            .bind("offset", (page - 1) * size)
            .bind("amount", size)
            .map { t ->
                ThreadInListProjection(
                    t["tid"] as Long,
                    t["title"] as String,
                    t["lastReplyTime"] as LocalDateTime,
                    t["postTime"] as LocalDateTime,
                    t["threadContent"] as String? ?: "",
                    t["uid"] as Long,
                    t["username"] as String,
                    (t["question"] as Byte).toBoolean(),
                    t["bestAnswer"] as Long?
                )
            }
            .all()
            .asFlow()
    }

    suspend fun findByTid(tid: Long): Mono<ThreadProjection> {
        val sql =
            "select sid, tid, title, threadContent, lastReplyTime, postTime, question, bestAnswer, u.uid , u.username from thread left join user u on thread.uid = u.uid where tid=:tid"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("tid", tid)
            .map { t ->
                ThreadProjection(
                    t["tid"] as Long,
                    t["sid"] as Long,
                    t["title"] as String,
                    t["threadContent"] as String? ?: "",
                    t["lastReplyTime"] as LocalDateTime,
                    t["postTime"] as LocalDateTime,
                    (t["question"] as Byte).toBoolean(),
                    t["bestAnswer"] as Long?,
                    t["uid"] as Long,
                    t["username"] as String
                )
            }
            .one()
    }

    suspend fun countBySid(sid: Long): Mono<Long> {
        return r2dbcEntityTemplate.count(Query.query(where("sid").`is`(sid)), ThreadEntity::class.java)
    }

    suspend fun searchInTitle(keyword: String, page: Long, amount: Long): Flow<SearchResultDTO> {
        val sql =
            "select tid,title,lastReplyTime,postTime,uid,username,question,bestAnswer from (select * from  thread  where  match  (title)  against  (:keyword IN NATURAL LANGUAGE MODE) ORDER BY lastReplyTime) as t natural join user limit :offset,:amount"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("keyword", keyword)
            .bind("offset", (page - 1) * amount)
            .bind("amount", amount)
            .fetch()
            .all()
            .map { t ->
                SearchResultDTO(
                    t["tid"] as Long,
                    t["title"] as String,
                    t["lastReplyTime"] as LocalDateTime,
                    t["postTime"] as LocalDateTime,
                    t["uid"] as Long,
                    t["username"] as String,
                    (t["question"] as Byte).toBoolean(),
                    t["bestAnswer"] as Long?
                )
            }
            .asFlow()
    }

    suspend fun findThreadEntityByTid(tid: Long): Mono<ThreadEntity> {
        return r2dbcEntityTemplate.select(Query.query(where("tid").`is`(tid)), ThreadEntity::class.java)
            .singleOrEmpty()
    }

    suspend fun insert(thread: ThreadEntity): Mono<ThreadEntity> {
        return r2dbcEntityTemplate.insert(thread)
    }

    suspend fun existsById(threadId: Long): Mono<Boolean> {
        return r2dbcEntityTemplate.exists(Query.query(where("tid").`is`(threadId)), ThreadEntity::class.java)
    }

    suspend fun update(thread: ThreadEntity): Mono<ThreadEntity> {
        return r2dbcEntityTemplate.update(thread)
    }

    suspend fun delete(thread: ThreadEntity): Mono<Int> {
        return r2dbcEntityTemplate.delete(Query.query(where("tid").`is`(thread.tid!!)), ThreadEntity::class.java)
    }
}