//package com.young_zy.forum.repo
//
//import com.young_zy.forum.model.user.DetailedUser
//import com.young_zy.forum.model.user.UserEntity
//
//interface UserRepository : JpaRepository<UserEntity, Int> {
//    fun findByUid(uid: Int): UserEntity?
//
//    fun findDetailedUserEntityByUid(uid: Int): DetailedUser?
//
//    fun findByUsername(username: String): UserEntity?
//
//    fun existsByUsername(username: String): Boolean
//}