package com.young_zy.forum.repo

import com.young_zy.forum.config.toBoolean
import com.young_zy.forum.model.message.DetailedMessage
import com.young_zy.forum.model.message.MessageEntity
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
class MessageNativeRepository {

    @Autowired
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    fun insertMessage(message: MessageEntity): Mono<MessageEntity> {
        return r2dbcEntityTemplate.insert(message)
    }

    suspend fun deleteMessage(messageId: Long): Mono<Int> {
        return r2dbcEntityTemplate.delete(
            Query.query(
                where("messageId").`is`(messageId)
            ),
            MessageEntity::class.java
        )
    }

    fun getAllByUid(userId: Long, page: Long, size: Long): Flow<DetailedMessage> {
        val sql =
            "select messageId, sender, username, messageText, unread, sendTime from `message` INNER JOIN `user` on `message`.sender = `user`.`uid` where uid=:userId order by unread DESC, messageId DESC LIMIT :offset,:size"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("userId", userId)
            .bind("offset", (page - 1) * size)
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

    fun updateMessage(message: MessageEntity): Mono<MessageEntity> {
        return r2dbcEntityTemplate.update(message)
    }

    suspend fun getMessageById(messageId: Long): Mono<MessageEntity> {
        return r2dbcEntityTemplate.select(
            Query.query(where("messageId").`is`(messageId)),
            MessageEntity::class.java
        ).singleOrEmpty()
    }

}