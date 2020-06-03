//package com.young_zy.forum.repo
//
//import com.young_zy.forum.model.section.SectionEntity
//import org.springframework.data.repository.kotlin.CoroutineCrudRepository
//
//interface SectionRepository : CoroutineCrudRepository<SectionEntity, Int> {
//    fun findSectionEntityBySid(sid: Int): SectionEntity?
//
//    fun existsBySectionName(sectionName: String): Boolean
//}