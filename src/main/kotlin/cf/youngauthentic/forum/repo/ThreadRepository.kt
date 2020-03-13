package cf.youngauthentic.forum.repo

import cf.youngauthentic.forum.model.thread.ThreadEntity
import cf.youngauthentic.forum.model.thread.ThreadInListProjection
import cf.youngauthentic.forum.model.thread.ThreadProjection
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ThreadRepository : JpaRepository<ThreadEntity, Int> {

    fun findAllBySid(sid: Int, page: Pageable): List<ThreadInListProjection>

    fun findByTid(tid: Int): ThreadProjection
}