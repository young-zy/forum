package cf.youngauthentic.forum.model.reply

import cf.youngauthentic.forum.model.thread.ThreadEntity
import cf.youngauthentic.forum.model.user.UserEntity
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "reply", schema = "Forum")
data class ReplyEntity(
        @Column(name = "rid", nullable = false, precision = 0)
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var rid: Int = 0,
        @Column(name = "tid", nullable = false)
        var tid: Int = 0,
        @Column(name = "replyContent", nullable = true)
        @Basic
        var replyContent: String? = null,
        @Column(name = "uid", nullable = true)
        @Basic
        var uid: Int,
        @Column(name = "replyTime", nullable = false)
        @Basic
        var replyTime: Timestamp = Timestamp(System.currentTimeMillis()),
        @Column(name = "lastEditTime", nullable = false)
        @Basic
        var lastEditTime: Timestamp = Timestamp(System.currentTimeMillis()),
        @Column(name = "priority", nullable = true, precision = 5, scale = 5)
        @Basic
        var priority: Double? = null,
        @Column(name = "is_bestAnswer", nullable = false)
        @Basic
        var bestAnswer: Boolean = false,
        @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false, insertable = false, updatable = false)
        @ManyToOne
        var threadByTid: ThreadEntity? = null,
        @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false, insertable = false, updatable = false)
        @ManyToOne
        var userByUid: UserEntity? = null
)