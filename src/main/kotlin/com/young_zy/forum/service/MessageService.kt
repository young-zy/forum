package com.young_zy.forum.service

import com.young_zy.forum.repo.MessageNativeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageService {

    @Autowired
    private lateinit var messageNativeRepository: MessageNativeRepository

    suspend fun postMessage(token: String, to: Long, messageText: String) {

    }

}