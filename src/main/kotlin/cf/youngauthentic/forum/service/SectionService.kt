package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.model.section.SectionEntity
import cf.youngauthentic.forum.model.section.SectionObject
import cf.youngauthentic.forum.repo.SectionRepository
import cf.youngauthentic.forum.repo.ThreadRepository
import cf.youngauthentic.forum.service.exception.AuthException
import cf.youngauthentic.forum.service.exception.NotAcceptableException
import cf.youngauthentic.forum.service.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.ceil

@Service
class SectionService {

    @Autowired
    private lateinit var sectionRepository: SectionRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var loginService: LoginService

    fun hasSection(sectionId: Int): Boolean {
        return sectionRepository.existsById(sectionId)
    }

    @Throws(AuthException::class, NotFoundException::class)
    fun getSection(token: String, sectionId: Int, page: Int = 1, size: Int = 10): SectionObject {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        val sectionEntity = sectionRepository.findSectionEntityBySid(sectionId)
                ?: throw NotFoundException("section $sectionId not found")
        val threads = threadRepository.findAllBySid(sectionId,
                PageRequest.of(page - 1, size, Sort.by("lastReplyTime").descending()))
        return SectionObject(
                sectionEntity,
                threads,
                page,
                ceil(threadRepository.countBySid(sectionId) / size.toDouble()).toInt()
        )
    }

    @Throws(AuthException::class)
    fun getSectionList(token: String): List<SectionEntity> {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.UN_LOGGED_IN))
        return sectionRepository.findAll()
    }

    @Throws(AuthException::class, NotAcceptableException::class)
    @Transactional
    fun addSection(token: String, sectionName: String) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        if (sectionRepository.existsBySectionName(sectionName)) {
            throw NotAcceptableException("section with $sectionName already exists")
        }
        val section = SectionEntity(sectionName = sectionName)
        sectionRepository.save(section)
    }

    @Throws(AuthException::class, NotFoundException::class)
    @Transactional
    fun deleteSection(token: String, sectionId: Int) {
        val tokenObj = loginService.getToken(token)
        authService.hasAuth(tokenObj, AuthConfig(AuthLevel.SYSTEM_ADMIN))
        val section = sectionRepository.findSectionEntityBySid(sectionId)
                ?: throw NotFoundException("sectionId $sectionId not found")
        sectionRepository.delete(section)
    }
}