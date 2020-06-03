package com.young_zy.forum.repo

import com.young_zy.forum.model.reply.ReplyEntity
import com.young_zy.forum.model.reply.ReplyObject
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.r2dbc.core.awaitRowsUpdated
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDateTime

@Repository
class ReplyNativeRepository {
    @Autowired
    private lateinit var r2dbcDatabaseClient: DatabaseClient

    suspend fun countByTid(tid: Int): Long {
        return r2dbcDatabaseClient.execute("select count(*) as count from reply where tid = :tid")
                .bind("tid", tid)
                .map { t -> t["count"] as Long }
                .one()
                .awaitFirst()
    }

    fun mapReplyObject(t: Row): ReplyObject {
        return ReplyObject(
                (t["rid"] as BigInteger).toInt(),
                t["replyContent"] as String,
                t["replyTime"] as LocalDateTime,
                t["lastEditTime"] as LocalDateTime,
                (t["priority"] as BigDecimal).toDouble(),
                (t["isBestAnswer"] as Byte).toInt() != 0,
                t["upVote"] as Int,
                t["uid"] as Long,
                t["username"] as String,
                t["vote"] as Int? ?: 0
        )
    }

    //select * from (select rid, replyContent, replyTime, lastEditTime, priority, isBestAnswer, upVote, downVote, uid, username from reply natural join user where tid=48) as reply left join (select rid,vote from vote where uid=1)as vote on reply.rid=vote.rid
    suspend fun findAllByTid(tid: Int, page: Int, size: Int): Flow<ReplyObject> {
//        return r2dbcDatabaseClient.select().from(ReplyEntity::class.java)
//                .matching(where("tid").`is`(tid))
//                .page(PageRequest.of(page, size))
//                .orderBy(Sort.by("priority").descending())
//                .fetch()
//                .all()
//                .asFlow()
        return r2dbcDatabaseClient.execute("select * from (select rid, replyContent, replyTime, lastEditTime, priority, isBestAnswer, upVote, downVote, uid, username from reply natural join user where tid=:tid) as reply left join (select rid,vote from vote where uid=1)as vote on reply.rid=vote.rid limit :offset,:size")
                .bind("tid", tid)
                .bind("offset", (page - 1) * size)
                .bind("size", size)
                .map { t ->
                    mapReplyObject(t)
                }
                .all()
                .asFlow()
    }

    suspend fun findReplyEntityByRid(rid: Int): ReplyEntity? {
        return r2dbcDatabaseClient.select()
                .from(ReplyEntity::class.java)
                .matching(where("rid").`is`(rid))
                .fetch()
                .awaitOneOrNull()
    }

    suspend fun findByRid(rid: Int, uid: Long): ReplyObject? {
        return r2dbcDatabaseClient.execute("select * from (select replyContent, replyTime, lastEditTime, priority, isBestAnswer, upVote, downVote, uid, username, rid from reply natural join user where rid=:rid) as reply left join (select rid, vote from vote where uid=:uid)as vote on reply.rid=vote.rid")
                .bind("rid", rid)
                .bind("uid", uid)
                .map { t ->
                    mapReplyObject(t)
                }
                .one()
                .awaitFirst()
    }

    suspend fun insert(replyEntity: ReplyEntity): Int {
        return r2dbcDatabaseClient.insert()
                .into(ReplyEntity::class.java)
                .using(replyEntity)
                .fetch()
                .one()
                .map { t ->
                    t["rid"] as Int
                }
                .awaitFirst()
    }

    suspend fun delete(replyEntity: ReplyEntity): Void? {
        return r2dbcDatabaseClient.delete()
                .from(ReplyEntity::class.java)
                .matching(where("rid").`is`(replyEntity.rid!!))
                .then()
                .awaitFirstOrNull()
    }

    suspend fun update(replyEntity: ReplyEntity): Void? {
        return r2dbcDatabaseClient.update()
                .table(ReplyEntity::class.java)
                .using(replyEntity)
                .then()
                .awaitFirstOrNull()
    }

    suspend fun deleteAllByTid(tid: Int): Int {
        return r2dbcDatabaseClient.delete()
                .from(ReplyEntity::class.java)
                .matching(where("tid").`is`(tid))
                .fetch()
                .awaitRowsUpdated()
    }

}