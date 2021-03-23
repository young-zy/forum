package com.young_zy.forum.repo

import com.young_zy.forum.model.section.SectionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.*
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class SectionNativeRepository {
//    @Autowired
//    private lateinit var r2dbcDatabaseClient: DatabaseClient

    @Autowired
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    suspend fun findSectionEntityBySid(sid: Long): Mono<SectionEntity> {
        return r2dbcEntityTemplate.select(Query.query(where("sid").`is`(sid)), SectionEntity::class.java)
            .singleOrEmpty()
    }

    suspend fun existsBySectionName(sectionName: String): Mono<Boolean> {
//        val sql = "select count(*) as count from section where section_name=:sectionName"
//        return r2dbcEntityTemplate.databaseClient.sql(sql)
//                .bind("sectionName", sectionName)
//                .map { t -> t["count"] as Long > 0 }
//                .one()
        return r2dbcEntityTemplate.exists(
            Query.query(where("section_name").`is`(sectionName)),
            SectionEntity::class.java
        )
    }

    suspend fun existsById(sid: Long): Mono<Boolean> {
        return r2dbcEntityTemplate.exists(Query.query(where("sid").`is`(sid)), SectionEntity::class.java)
//        return r2dbcDatabaseClient.execute("select count(*) as count from section where sid=:sid")
//                .bind("sid", sid)
//                .map { t -> t["count"] as Long > 0 }
//                .awaitOne()
    }

    suspend fun delete(section: SectionEntity): Mono<Int> {
        return r2dbcEntityTemplate.delete(
            Query.query(where("sectionName").`is`(section.sectionName!!)),
            SectionEntity::class.java
        )
    }

    suspend fun insert(section: SectionEntity): Mono<SectionEntity> {
        return r2dbcEntityTemplate.insert(section)
    }

    suspend fun findAll(): Flow<SectionEntity> {
        return r2dbcEntityTemplate.select(SectionEntity::class.java)
            .all()
            .asFlow()
    }

}