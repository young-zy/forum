package com.young_zy.forum.repo

import com.young_zy.forum.model.vote.VoteEntity
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitFirst
import org.springframework.stereotype.Repository

@Repository
class VoteNativeRepository {
    @Autowired
    private lateinit var r2dbcDatabaseClient: DatabaseClient

    suspend fun findVoteEntityByUidAndRid(uid: Long, rid: Int): VoteEntity? {
        return r2dbcDatabaseClient.execute("select uid, rid, vote from vote where uid=:uid and rid=:rid")
                .`as`(VoteEntity::class.java)
                .bind("uid", uid)
                .bind("rid", rid)
                .fetch()
                .awaitFirst()
    }

    suspend fun save(voteEntity: VoteEntity): Void? {
        return r2dbcDatabaseClient.execute("replace into vote(uid, rid, vote) VALUES (:uid, :rid, :vote) ")
                .bind("uid", voteEntity.uid)
                .bind("rid", voteEntity.rid)
                .bind("vote", voteEntity.vote)
                .then()
                .awaitFirstOrNull()
    }
}