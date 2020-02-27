package cf.youngauthentic.forum.model

import cf.youngauthentic.forum.model.user.UserEntity
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "Reply", schema = "Forum")
data class ReplyEntity(
        @Column(name = "rid", nullable = false, precision = 0)
        @Id
        var rid: Int = 0,
        @Column(name = "replyContent", nullable = true, length = -1)
        @Basic
        var replyContent: String? = null,
        @Column(name = "replyTime", nullable = false)
        @Basic
        var replyTime: Timestamp? = null,
        @Column(name = "lastEditTime", nullable = false)
        @Basic
        var lastEditTime: Timestamp? = null,
        @Column(name = "priority", nullable = true)
        @Basic
        var priority: Any? = null,
        @Column(name = "is_bestAnswer", nullable = false)
        @Basic
        var isBestAnswer: Byte = 0,
        @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false)
        @ManyToOne
        var threadByTid: ThreadEntity? = null,
        @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false)
        @ManyToOne
        var userByUid: UserEntity? = null
)