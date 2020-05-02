package com.young_zy.forum.model.user

import com.google.gson.Gson
import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "user", schema = "Forum")
data class UserEntity(
        @Column(name = "uid", nullable = false)
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        override var uid: Int = 0,
        @Column(name = "username", nullable = false, length = 45)
        @Basic
        override var username: String = "",
        @Column(name = "email", nullable = false, length = 45)
        @Basic
        var email: String = "",
        @Column(name = "hashedPassword", nullable = false, length = 600)
        @Basic
        var hashedPassword: String = "",
        @Column(name = "regDate", nullable = false)
        @Basic
        var regDate: Date = Date(0),
        @Column(name = "auth", nullable = false)
        @Convert(converter = UserAuthConverter::class)
        var auth: UserAuth = UserAuth(),
        @Column(name = "tag_priority", nullable = false)
        @Basic
        var tagPriority: String = "0"
) : SimpleUser

@Converter
class UserAuthConverter : AttributeConverter<UserAuth, String> {

        private val gson = Gson()

        override fun convertToDatabaseColumn(attribute: UserAuth?): String {
                return gson.toJson(attribute)
        }

        override fun convertToEntityAttribute(dbData: String?): UserAuth {
                return gson.fromJson(dbData, UserAuth::class.java)
        }

}