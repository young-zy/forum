package com.young_zy.forum.repo

import com.young_zy.forum.model.reply.ReplyEntity
import com.young_zy.forum.model.reply.ReplyProjection
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource


@RepositoryRestResource(exported = false)
interface ReplyRepository : JpaRepository<ReplyEntity, Int> {
    fun findAllByTid(tid: Int, pageable: Pageable): List<ReplyProjection>

    fun findByRid(rid: Int): ReplyEntity?

    fun findProjectionByRid(rid: Int): ReplyProjection?

    fun deleteAllByTid(tid: Int)

    fun countByTid(tid: Int): Int
}