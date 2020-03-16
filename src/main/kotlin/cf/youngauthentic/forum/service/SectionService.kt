package cf.youngauthentic.forum.service

import cf.youngauthentic.forum.repo.SectionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SectionService {

    @Autowired
    lateinit var sectionRepository: SectionRepository

    fun hasSection(sectionId: Int): Boolean {
        return sectionRepository.existsById(sectionId)
    }
}