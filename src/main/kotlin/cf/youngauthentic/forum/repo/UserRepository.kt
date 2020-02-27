package cf.youngauthentic.forum.repo

import cf.youngauthentic.forum.model.user.DetailedUser
import cf.youngauthentic.forum.model.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Int> {
    fun findByUid(uid: Int): UserEntity

    fun findDetailedUserEntityByUid(uid: Int): DetailedUser

    fun findByUsername(username: String): UserEntity?

    fun existsByUsername(username: String): Boolean
}