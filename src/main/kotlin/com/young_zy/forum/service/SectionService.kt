package com.young_zy.forum.service

import com.young_zy.forum.model.section.SectionEntity
import com.young_zy.forum.model.section.SectionObject
import com.young_zy.forum.repo.SectionNativeRepository
import com.young_zy.forum.repo.ThreadNativeRepository
import com.young_zy.forum.common.exception.ForbiddenException
import com.young_zy.forum.common.exception.ConflictException
import com.young_zy.forum.common.exception.NotFoundException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
import org.casbin.jcasbin.main.Enforcer
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

    @Autowired
    private lateinit var enforcer: Enforcer

    suspend fun hasSection(sectionId: Long): Boolean {
        return sectionNativeRepository.existsById(sectionId).awaitSingle()
    }

    @Throws(ForbiddenException::class, NotFoundException::class)
    suspend fun getSection(sectionId: Long, page: Long = 1, size: Long = 10): SectionObject {
//        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        val sectionEntity = sectionNativeRepository.findSectionEntityBySid(sectionId)
//        val threads = mutableListOf<ThreadInListProjection>()
        val threads = threadNativeRepository.findAllBySid(sectionId, page, size)
        return SectionObject(
            sectionEntity.awaitSingleOrNull() ?: throw NotFoundException("section $sectionId not found"),
            threads.toList(),
            page,
            ceil(threadNativeRepository.countBySid(sectionId).awaitSingle() / size.toDouble()).toInt().coerceAtLeast(1)
        )
    }

    @Throws(ForbiddenException::class)
    suspend fun getSectionList(): List<SectionEntity> {
        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        return sectionNativeRepository.findAll().toList()
    }

    @Throws(ForbiddenException::class, ConflictException::class)
    suspend fun addSection(sectionName: String) {
        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        if (!enforcer.enforce(tokenObj, null, "addSection")) {
            throw ForbiddenException("user ${tokenObj?.uid} is not allowed to do the operation, minimum auth is system admin")
        }
        transactionOperator.executeAndAwait {
            if (sectionNativeRepository.existsBySectionName(sectionName).awaitSingle()) {
                throw ConflictException("section with $sectionName already exists")
            }
            val section = SectionEntity(sectionName = sectionName)
            sectionNativeRepository.insert(section).awaitSingle()
        }
    }

    @Throws(ForbiddenException::class, NotFoundException::class)
    suspend fun deleteSection(sectionId: Long) {
        val tokenObj = loginService.getToken()
//        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        if (!enforcer.enforce(tokenObj, null, "addSection")) {
            throw ForbiddenException("user ${tokenObj?.uid} is not allowed to do the operation, minimum auth is system admin")
        }
        transactionOperator.executeAndAwait {
            val section = sectionNativeRepository.findSectionEntityBySid(sectionId).awaitSingleOrNull()
                ?: throw NotFoundException("sectionId $sectionId not found")
            sectionNativeRepository.delete(section).awaitSingle()
        }
    }
}