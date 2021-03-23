package com.young_zy.forum.repo

import com.young_zy.forum.model.vote.VoteEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class VoteNativeRepository {
//    @Autowired
//    private lateinit var r2dbcDatabaseClient: DatabaseClient

    @Autowired
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    suspend fun findVoteEntityByUidAndRid(uid: Long, rid: Long): Mono<VoteEntity> {
        val sql = "select uid, rid, vote from vote where uid=:uid and rid=:rid"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("uid", uid)
            .bind("rid", rid)
            .map { r ->
                VoteEntity(
                    r["uid"] as Long,
                    r["rid"] as Long,
                    r["vote"] as Long
                )
            }
            .one()
    }

    suspend fun save(voteEntity: VoteEntity): Mono<Void> {
        val sql = "replace into vote(uid, rid, vote) VALUES (:uid, :rid, :vote)"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("uid", voteEntity.uid)
            .bind("rid", voteEntity.rid)
            .bind("vote", voteEntity.vote)
            .then()
    }
}