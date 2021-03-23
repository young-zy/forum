package com.young_zy.forum.repo

import com.young_zy.forum.model.user.DetailedUser
import com.young_zy.forum.model.user.UserAuth
import com.young_zy.forum.model.user.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDate

@Repository
class UserNativeRepository {

//    @Autowired
//    private lateinit var r2dbcDatabaseClient: DatabaseClient

    @Autowired
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    fun findAllDetailedUser(page: Int, size: Int): Flow<DetailedUser> {
        val sql = "select uid, username, email, auth, regdate from user limit :offset, :size"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("offset", (page - 1) * size)
            .bind("size", size)
            .map { t ->
                DetailedUser(
                    t["uid"] as Long,
                    t["username"] as String,
                    t["email"] as String,
                    UserAuth.build(t["auth"] as String),
                    t["regdate"] as LocalDate
                )
            }
            .all()
            .asFlow()
    }

    fun findByUid(uid: Long): Mono<UserEntity> {
        return r2dbcEntityTemplate
            .select(Query.query(where("uid").`is`(uid)), UserEntity::class.java)
            .single()
    }

    suspend fun findByUsername(username: String): Mono<UserEntity> {
        return r2dbcEntityTemplate
            .select(Query.query(where("username").`is`(username)), UserEntity::class.java)
            .single()
    }

    suspend fun existsByUsername(username: String): Mono<Boolean> {
        return r2dbcEntityTemplate
            .exists(Query.query(where("username").`is`(username)), DetailedUser::class.java)
    }

    suspend fun existsByEmail(email: String): Mono<Boolean> {
        return r2dbcEntityTemplate
            .exists(Query.query(where("email").`is`(email)), UserEntity::class.java)
    }

    suspend fun insert(user: UserEntity): Mono<UserEntity> {
        return r2dbcEntityTemplate
            .insert(user)
    }

    suspend fun update(user: UserEntity): Mono<UserEntity> {
        return r2dbcEntityTemplate.update(user)
//                .table(UserEntity::class.java)
//                .using(user)
//                .then()
//                .awaitFirstOrNull()
    }

    suspend fun findDetailedUserEntityByUid(uid: Long): Mono<DetailedUser> {
        val sql = "select uid, username, email, auth, regdate from user where uid = :uid"
        return r2dbcEntityTemplate.databaseClient.sql(sql)
            .bind("uid", uid)
            .map { t ->
                DetailedUser(
                    t["uid"] as Long,
                    t["username"] as String,
                    t["email"] as String,
                    UserAuth.build(t["auth"] as String),
                    t["regdate"] as LocalDate
                )
            }
            .one()
    }
}