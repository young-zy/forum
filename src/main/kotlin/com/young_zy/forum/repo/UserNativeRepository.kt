package com.young_zy.forum.repo

import com.young_zy.forum.model.user.DetailedUser
import com.young_zy.forum.model.user.UserAuth
import com.young_zy.forum.model.user.UserEntity
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class UserNativeRepository {

    @Autowired
    private lateinit var r2dbcDatabaseClient: DatabaseClient

    suspend fun findByUid(uid: Long): UserEntity? {
        return r2dbcDatabaseClient
                .select()
                .from(UserEntity::class.java)
                .matching(where("uid").`is`(uid))
                .fetch()
                .awaitOneOrNull()
    }

    suspend fun findByUsername(username: String): UserEntity? {
        return r2dbcDatabaseClient
                .select()
                .from(UserEntity::class.java)
                .matching(where("username").`is`(username))
                .fetch()
                .awaitOneOrNull()
    }

    suspend fun existsByUsername(username: String): Boolean {
        return r2dbcDatabaseClient
                .select()
                .from("user")
                .`as`(DetailedUser::class.java)
                .matching(where("username").`is`(username))
                .fetch()
                .awaitOneOrNull() != null
    }

    suspend fun insert(user: UserEntity): Void? {
        return r2dbcDatabaseClient
                .insert()
                .into(UserEntity::class.java)
                .using(user)
                .then()
                .awaitFirst()
    }

    suspend fun update(user: UserEntity): Void? {
        return r2dbcDatabaseClient.update()
                .table(UserEntity::class.java)
                .using(user)
                .then()
                .awaitFirstOrNull()
    }

    suspend fun findDetailedUserEntityByUid(uid: Long): DetailedUser? {
        return r2dbcDatabaseClient.execute("select uid, username, email, auth, regdate from user where uid = :uid")
                .bind("uid", uid)
                .map { t ->
                    DetailedUser(
                            t["uid"] as Long,
                            t["username"] as String,
                            t["email"] as String,
                            UserAuth.build(t["auth"] as String),
                            t["regDate"] as LocalDate
                    )
                }
                .awaitOneOrNull()
    }
}