package cf.youngauthentic.forum.repo

import cf.youngauthentic.forum.model.section.SectionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SectionRepository : JpaRepository<SectionEntity, Int>