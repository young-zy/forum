package com.young_zy.forum.service

import com.young_zy.forum.model.message.DetailedMessage
import com.young_zy.forum.model.message.MessageEntity
import com.young_zy.forum.repo.MessageNativeRepository
import com.young_zy.forum.service.exception.NotFoundException
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class MessageService {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var messageNativeRepository: MessageNativeRepository

    @Autowired
    private lateinit var transactionalOperator: TransactionalOperator

    @Autowired
    private lateinit var loginService: LoginService

    @Autowired
    private lateinit var authService: AuthService

    suspend fun postMessage(token: String, to: Long, messageText: String) {
        logger.info("post message start")
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        transactionalOperator.executeAndAwait {
            logger.info("post message transaction start")
            val message = MessageEntity(
                    from = tokenObj!!.uid,
                    to = to,
                    messageText = messageText
            )
            messageNativeRepository.insertMessage(message)
            logger.info("post message transaction end")
        }
        logger.info("post message end")
    }

    suspend fun deleteMessage(token: String, messageId: Long){
        val tokenObj = loginService.getToken(token)
        transactionalOperator.executeAndAwait {
            val message = messageNativeRepository.getMessageById(messageId) ?: throw NotFoundException("message with messageId $messageId not found")
            authService.hasAuth(tokenObj, AuthConfig(
                    authLevel =  AuthLevel.USER,
                    allowOnlyAuthor = true,
                    authorUid = message.to
            ))
            messageNativeRepository.deleteMessage(messageId)
        }
    }

    suspend fun setReadState(token: String, messageId: Long, readState: Boolean){
        val tokenObj = loginService.getToken(token)
        transactionalOperator.executeAndAwait {
            val message = messageNativeRepository.getMessageById(messageId) ?: throw NotFoundException("message with messageId $messageId not found")
            authService.hasAuth(tokenObj, AuthConfig(
                    authLevel =  AuthLevel.USER,
                    allowOnlyAuthor = true,
                    authorUid = message.to
            ))
            message.unread = readState
            messageNativeRepository.updateMessage(message)
        }
    }

    suspend fun getAllMessage(token: String, page: Long, size: Long): List<DetailedMessage> {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(
                authLevel =  AuthLevel.BLOCKED_USER
        ))
        return messageNativeRepository.getAllByUid(tokenObj!!.uid, page, size).toList()
    }

}