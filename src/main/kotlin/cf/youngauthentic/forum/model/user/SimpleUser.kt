package cf.youngauthentic.forum.model.user

import org.springframework.data.rest.core.config.Projection

@Projection(types = [UserEntity::class])
interface SimpleUser {
    fun getUid(): Int
    fun getUsername(): String
}