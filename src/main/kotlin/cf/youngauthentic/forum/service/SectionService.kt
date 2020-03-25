package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.model.section.SectionObject
import cf.youngauthentic.forum.repo.SectionRepository
import cf.youngauthentic.forum.repo.ThreadRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class SectionService {

    @Autowired
    private lateinit var sectionRepository: SectionRepository

    @Autowired
    private lateinit var threadRepository: ThreadRepository

    fun hasSection(sectionId: Int): Boolean {
        return sectionRepository.existsById(sectionId)
    }

    fun getSection(sectionId: Int, page: Int = 1, size: Int = 10): SectionObject {
        val sectionEntity = sectionRepository.findSectionEntityBySid(sectionId)
        val threads = threadRepository.findAllBySid(sectionId,
                PageRequest.of(page - 1, size, Sort.by("lastReplyTime").descending()))
        return SectionObject(
                sectionEntity,
                threads,
                page,
                ceil(threadRepository.countBySid(sectionId) / size.toDouble()).toInt()
        )
    }
}