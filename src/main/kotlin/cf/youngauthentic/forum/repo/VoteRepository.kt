package cf.youngauthentic.forum.repo

import cf.youngauthentic.forum.model.vote.VoteEntity
import cf.youngauthentic.forum.model.vote.VoteEntityPK
import org.springframework.data.jpa.repository.JpaRepository

interface VoteRepository : JpaRepository<VoteEntity, VoteEntityPK> {
    fun findVoteEntityByUidAndRid(uid: Int, rid: Int): VoteEntity
}