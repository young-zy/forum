package com.young_zy.forum.repo

import com.young_zy.forum.model.thread.ThreadEntity
import com.young_zy.forum.model.thread.ThreadInListProjection
import com.young_zy.forum.model.thread.ThreadProjection
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface ThreadRepository : JpaRepository<ThreadEntity, Int> {

    fun findAllBySid(sid: Int, page: Pageable): List<ThreadInListProjection>

    fun findByTid(tid: Int): ThreadProjection?

    fun findThreadEntityByTid(tid: Int): ThreadEntity?

    fun countBySid(sid: Int): Int


    @Query(value = "select tid,title,lastReplyTime,postTime,uid,username,question,hasBestAnswer from (select * from  thread  where  match  (title)  against  (?1 IN NATURAL LANGUAGE MODE) ORDER BY lastReplyTime) as t natural join user /*#pageable*/",
            nativeQuery = true,
            countQuery = "SELECT count(*) from  thread  where  match  (title)  against  (?1 IN NATURAL LANGUAGE MODE)",
            name = "searchResultDTO")
    fun searchInTitle(keyWord: String, pageable: Pageable): List<Array<Any>>

}