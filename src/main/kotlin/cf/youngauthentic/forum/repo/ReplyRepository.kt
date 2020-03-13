package cf.youngauthentic.forum.repo

import cf.youngauthentic.forum.model.reply.ReplyEntity
import cf.youngauthentic.forum.model.reply.ReplyProjection
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReplyRepository : JpaRepository<ReplyEntity, Int> {
    fun findAllByTid(tid: Int, pageable: Pageable): List<ReplyProjection>
}