package com.young_zy.forum.service

import com.young_zy.forum.common.exception.ForbiddenException
import com.young_zy.forum.model.message.DetailedMessage
import com.young_zy.forum.model.message.MessageEntity
import com.young_zy.forum.repo.MessageNativeRepository
import com.young_zy.forum.common.exception.NotFoundException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingleOrNull
import org.casbin.jcasbin.main.Enforcer
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

    @Autowired
    private lateinit var enforcer: Enforcer

    suspend fun postMessage(to: Long, messageText: String) {
        logger.info("post message start")
        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.USER))
        if (!enforcer.enforce(tokenObj, "", "postMessage")) {
            throw ForbiddenException("permission denied")
        }
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

    suspend fun deleteMessage(messageId: Long) {
        val tokenObj = loginService.getToken()
        transactionalOperator.executeAndAwait {
            val message = messageNativeRepository.getMessageById(messageId).awaitSingleOrNull()
                ?: throw NotFoundException("message with messageId $messageId not found")
//            authService.hasAuth(tokenObj, AuthConfig(
//                    authLevel =  AuthLevel.USER,
//                    allowOnlyAuthor = true,
//                    authorUid = message.to
//            ))
            if (!enforcer.enforce(tokenObj, message, "deleteMessage")) {
                throw ForbiddenException("permission denied")
            }
            messageNativeRepository.deleteMessage(messageId)
        }
    }

    suspend fun setReadState(messageId: Long, readState: Boolean) {
        val tokenObj = loginService.getToken()
        transactionalOperator.executeAndAwait {
            val message = messageNativeRepository.getMessageById(messageId).awaitSingleOrNull()
                ?: throw NotFoundException("message with messageId $messageId not found")
//            authService.hasAuth(tokenObj, AuthConfig(
//                    authLevel =  AuthLevel.USER,
//                    allowOnlyAuthor = true,
//                    authorUid = message.to
//            ))
            if (!enforcer.enforce(tokenObj, message, "deleteMessage")) {
                throw ForbiddenException("permission denied")
            }
            message.unread = readState
            messageNativeRepository.updateMessage(message)
        }
    }

    suspend fun getAllMessage(page: Long, size: Long): List<DetailedMessage> {
        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(
//                authLevel =  AuthLevel.BLOCKED_USER
//        ))
        if (!enforcer.enforce(tokenObj, "", "getAllMessage")) {
            throw ForbiddenException("permission denied")
        }
        return messageNativeRepository.getAllByUid(tokenObj!!.uid, page, size).toList()
    }

}