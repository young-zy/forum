package com.young_zy.forum.repo

import com.young_zy.forum.model.section.SectionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface SectionRepository : JpaRepository<SectionEntity, Int> {
    fun findSectionEntityBySid(sid: Int): SectionEntity?

    fun existsBySectionName(sectionName: String): Boolean
}