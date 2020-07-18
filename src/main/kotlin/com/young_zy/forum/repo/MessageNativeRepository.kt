package com.young_zy.forum.repo

import com.young_zy.forum.model.message.DetailedMessage
import com.young_zy.forum.model.message.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.data.r2dbc.core.awaitRowsUpdated
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.stereotype.Repository

@Repository
class MessageNativeRepository {

    @Autowired
    private lateinit var r2dbcDatabaseClient: DatabaseClient

    suspend fun insertMessage(message: MessageEntity): Long? {
        return r2dbcDatabaseClient.insert()
                .into(MessageEntity::class.java)
                .using(message)
                .map { t ->
                    t["LAST_INSERT_ID"] as Long
                }
                .awaitOne()
    }

    suspend fun deleteMessage(messageId: Long): Int {
        return r2dbcDatabaseClient.delete()
                .from(MessageEntity::class.java)
                .matching(where("messageId").`is`(messageId))
                .fetch()
                .awaitRowsUpdated()
    }

    suspend fun getAllByUid(userId: Long, page: Int, size: Int): Flow<DetailedMessage> {
        return r2dbcDatabaseClient.execute("select messageId, `from`, `to`, messageText, unread, sendTime, uid, username, email from `message` INNER JOIN `user` on `message`.`from` = `user`.`uid` where uid=:userId")
                .bind("userId", userId)
                .`as`(DetailedMessage::class.java)
                .fetch()
                .all()
                .asFlow()
    }

}