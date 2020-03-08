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
        @GeneratedValue(strategy = GenerationType.AUTO)
        var rid: Int = 0,
        @Column(name = "tid", nullable = false)
        var tid: Int = 0,
        @Column(name = "replyContent", nullable = true)
        @Basic
        var replyContent: String? = null,
        @Column(name = "replyTime", nullable = false)
        @Basic
        var replyTime: Timestamp = Timestamp(System.currentTimeMillis()),
        @Column(name = "lastEditTime", nullable = false)
        @Basic
        var lastEditTime: Timestamp = Timestamp(System.currentTimeMillis()),
        @Column(name = "priority", nullable = true)
        @Basic
        var priority: Int? = null,
        @Column(name = "is_bestAnswer", nullable = false)
        @Basic
        var isBestAnswer: Boolean = false,
        @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false, insertable = false, updatable = false)
        @ManyToOne
        var threadByTid: ThreadEntity? = null,
        @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false)
        @ManyToOne
        var userByUid: UserEntity? = null
)