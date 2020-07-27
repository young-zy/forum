package com.young_zy.forum.repo

import com.young_zy.forum.config.toBoolean
import com.young_zy.forum.model.message.DetailedMessage
import com.young_zy.forum.model.message.MessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.r2dbc.core.awaitRowsUpdated
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

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

    suspend fun getAllByUid(userId: Long, page: Long, size: Long): Flow<DetailedMessage> {
        return r2dbcDatabaseClient.execute("select messageId, sender, username, messageText, unread, sendTime from `message` INNER JOIN `user` on `message`.sender = `user`.`uid` where uid=:userId order by unread DESC, messageId DESC LIMIT :offset,:size")
                .bind("userId", userId)
                .bind("offset", (page-1)*size)
                .bind("size", size)
                .map { r ->
                    DetailedMessage(
                            r["messageId"] as Long,
                            r["sender"] as Long,
                            r["username"] as String,
                            r["messageText"] as String,
                            (r["unread"] as Byte).toBoolean(),
                            r["sendTime"] as LocalDateTime
                    )
                }
                .all()
                .asFlow()
    }

    suspend fun updateMessage(message: MessageEntity): Int {
        return r2dbcDatabaseClient.update()
                .table(MessageEntity::class.java)
                .using(message)
                .fetch()
                .awaitRowsUpdated()
    }

    suspend fun getMessageById(messageId: Long): MessageEntity? {
        return r2dbcDatabaseClient.select()
                .from(MessageEntity::class.java)
                .matching(where("messageId").`is`(messageId))
                .fetch()
                .awaitOneOrNull()
    }
}