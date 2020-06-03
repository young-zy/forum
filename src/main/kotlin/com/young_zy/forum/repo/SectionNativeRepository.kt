package com.young_zy.forum.repo

import com.young_zy.forum.model.section.SectionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOne
import org.springframework.data.r2dbc.core.awaitRowsUpdated
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.stereotype.Repository

@Repository
class SectionNativeRepository {
    @Autowired
    private lateinit var r2dbcDatabaseClient: DatabaseClient

    suspend fun findSectionEntityBySid(sid: Int): SectionEntity? {
        return r2dbcDatabaseClient.select()
                .from(SectionEntity::class.java)
                .matching(where("sid").`is`(sid))
                .fetch()
                .awaitOne()
    }

    suspend fun existsBySectionName(sectionName: String): Boolean {
        return r2dbcDatabaseClient.execute("select count(*) from section where section_name=:sectionName")
                .bind("sectionName", sectionName)
                .map { t -> t["count"] as Int > 0 }
                .awaitOne()
    }

    suspend fun existsById(sid: Int): Boolean {
        return r2dbcDatabaseClient.execute("select count(*) as count from section where sid=:sid")
                .bind("sid", sid)
                .map { t -> t["count"] as Long > 0 }
                .awaitOne()
    }

    suspend fun delete(section: SectionEntity): Int {
        return r2dbcDatabaseClient.delete().from(SectionEntity::class.java)
                .matching(where("sectionName").`is`(section.sectionName!!))
                .fetch()
                .awaitRowsUpdated()
    }

    suspend fun insert(section: SectionEntity): Void? {
        return r2dbcDatabaseClient.insert()
                .into(SectionEntity::class.java)
                .using(section)
                .then()
                .awaitFirstOrNull()
    }

    suspend fun findAll(): Flow<SectionEntity> {
        return r2dbcDatabaseClient.select()
                .from(SectionEntity::class.java)
                .fetch()
                .all()
                .asFlow()
    }

}