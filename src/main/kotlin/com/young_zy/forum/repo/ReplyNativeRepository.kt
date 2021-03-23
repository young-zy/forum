package com.young_zy.forum.repo

import com.young_zy.forum.config.toBoolean
import com.young_zy.forum.model.reply.ReplyEntity
import com.young_zy.forum.model.reply.ReplyObject
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query.query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
class ReplyNativeRepository {

    @Autowired
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    suspend fun countByTid(tid: Long): Mono<Long> {
        return r2dbcEntityTemplate.count(query(where("tid").`is`(tid)), ReplyEntity::class.java)
    }

    private fun mapReplyObject(t: Row): ReplyObject {
        return ReplyObject(
            t["rid"] as Long,
            t["replyContent"] as String,
            t["replyTime"] as LocalDateTime,
            t["lastEditTime"] as LocalDateTime,
            (t["priority"] as BigDecimal).toDouble(),
            (t["isBestAnswer"] as Byte).toBoolean(),
            t["upVote"] as Int,
            t["uid"] as Long,
            t["username"] as String,
            t["vote"] as Int? ?: 0
        )
    }

    //select * from (select rid, replyContent, replyTime, lastEditTime, priority, isBestAnswer, upVote, downVote, uid, username from reply natural join user where tid=48) as reply left join (select rid,vote from vote where uid=1)as vote on reply.rid=vote.rid
    suspend fun findAllByTid(tid: Long, uid: Long, page: Int, size: Int): Flow<ReplyObject> {
        val sql =
            "select * from (select rid, replyContent, replyTime, lastEditTime, priority, isBestAnswer, upVote, downVote, uid, username from reply natural join user where tid=:tid) as reply left join (select rid,vote from vote where uid=:uid)as vote on reply.rid=vote.rid order by priority desc limit :offset,:size"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("tid", tid)
            .bind("offset", (page - 1) * size)
            .bind("size", size)
            .bind("uid", uid)
            .map { t ->
                mapReplyObject(t)
            }
            .all()
            .asFlow()
    }

    suspend fun findReplyEntityByRid(rid: Long): Mono<ReplyEntity> {
        return r2dbcEntityTemplate.select(query(where("rid").`is`(rid)), ReplyEntity::class.java)
            .singleOrEmpty()
    }

    suspend fun findByRid(rid: Int, uid: Long): Mono<ReplyObject> {
        val sql =
            "select * from (select replyContent, replyTime, lastEditTime, priority, isBestAnswer, upVote, downVote, uid, username, rid from reply natural join user where rid=:rid) as reply left join (select rid, vote from vote where uid=:uid)as vote on reply.rid=vote.rid"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("rid", rid)
            .bind("uid", uid)
            .map { t ->
                mapReplyObject(t)
            }
            .one()
    }

    suspend fun insert(replyEntity: ReplyEntity): Mono<ReplyEntity> {
        return r2dbcEntityTemplate.insert(replyEntity)
    }

    suspend fun delete(replyEntity: ReplyEntity): Mono<Int> {
        return r2dbcEntityTemplate.delete(query(where("rid").`is`(replyEntity.rid!!)), ReplyEntity::class.java)
    }

    suspend fun update(replyEntity: ReplyEntity): Mono<ReplyEntity> {
        return r2dbcEntityTemplate.update(replyEntity)
    }

    suspend fun deleteAllByTid(tid: Long): Mono<Int> {
        return r2dbcEntityTemplate.delete(query(where("tid").`is`(tid)), ReplyEntity::class.java)
    }

}