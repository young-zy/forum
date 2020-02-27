package cf.youngauthentic.forum.model.user

import java.sql.Date
import javax.persistence.*

@Entity
@Table(name = "User", schema = "Forum")
data class UserEntity(
        @Column(name = "uid", nullable = false)
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        var uid: Int = 0,
        @Column(name = "username", nullable = false, length = 45)
        @Basic
        var username: String = "",
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
        @Basic
        var auth: String = "unLoggedIn",
        @Column(name = "tag_priority", nullable = false)
        @Basic
        var tagPriority: String = "0"
)