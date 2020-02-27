package cf.youngauthentic.forum.model.user

import org.springframework.data.rest.core.config.Projection

@Projection(types = [UserEntity::class], name = "DetailedUser")
interface DetailedUser {
    fun getUid(): Int
    fun getUsername(): String
    fun getEmail(): String
    fun getAuth(): String
    fun getRegDate(): String
}