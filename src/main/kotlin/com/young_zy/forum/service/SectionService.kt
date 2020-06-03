package com.young_zy.forum.service

import com.young_zy.forum.model.section.SectionEntity
import com.young_zy.forum.model.section.SectionObject
import com.young_zy.forum.model.thread.ThreadInListProjection
import com.young_zy.forum.repo.SectionNativeRepository
import com.young_zy.forum.repo.ThreadNativeRepository
import com.young_zy.forum.service.exception.AuthException
import com.young_zy.forum.service.exception.NotAcceptableException
import com.young_zy.forum.service.exception.NotFoundException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import kotlin.math.ceil

@Service
class SectionService {

    @Autowired
    private lateinit var sectionNativeRepository: SectionNativeRepository

    @Autowired
    private lateinit var threadNativeRepository: ThreadNativeRepository

    @Autowired
    private lateinit var transactionOperator: TransactionalOperator

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var loginService: LoginService

    suspend fun hasSection(sectionId: Int): Boolean {
        return sectionNativeRepository.existsById(sectionId)
    }

    @Throws(AuthException::class, NotFoundException::class)
    suspend fun getSection(token: String, sectionId: Int, page: Int = 1, size: Int = 10): SectionObject {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        val sectionEntity = sectionNativeRepository.findSectionEntityBySid(sectionId)
                ?: throw NotFoundException("section $sectionId not found")
        val threads = mutableListOf<ThreadInListProjection>()
        threadNativeRepository.findAllBySid(sectionId, page, size).collect {
            threads.add(it)
        }
        return SectionObject(
                sectionEntity,
                threads,
                page,
                ceil(threadNativeRepository.countBySid(sectionId) / size.toDouble()).toInt()
        )
    }

    @Throws(AuthException::class)
    suspend fun getSectionList(token: String): Flow<SectionEntity> {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        return sectionNativeRepository.findAll()
    }

    @Throws(AuthException::class, NotAcceptableException::class)
    suspend fun addSection(token: String, sectionName: String) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        transactionOperator.executeAndAwait {
            if (sectionNativeRepository.existsBySectionName(sectionName)) {
                throw NotAcceptableException("section with $sectionName already exists")
            }
            val section = SectionEntity(sectionName = sectionName)
            sectionNativeRepository.insert(section)
        }
    }

    @Throws(AuthException::class, NotFoundException::class)
    suspend fun deleteSection(token: String, sectionId: Int) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        transactionOperator.executeAndAwait {
            val section = sectionNativeRepository.findSectionEntityBySid(sectionId)
                    ?: throw NotFoundException("sectionId $sectionId not found")
            sectionNativeRepository.delete(section)
        }
    }
}