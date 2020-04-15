package com.young_zy.forum.repo

import com.young_zy.forum.model.user.DetailedUser
import com.young_zy.forum.model.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

@RepositoryRestResource(exported = false)
interface UserRepository : JpaRepository<UserEntity, Int> {
    fun findByUid(uid: Int): UserEntity?

    fun findDetailedUserEntityByUid(uid: Int): DetailedUser?

    fun findByUsername(username: String): UserEntity?

    fun existsByUsername(username: String): Boolean
}