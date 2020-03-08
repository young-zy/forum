package cf.youngauthentic.forum.model.thread

import cf.youngauthentic.forum.model.reply.ReplyEntity
import cf.youngauthentic.forum.model.user.UserEntity
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "thread", schema = "Forum")
data class ThreadEntity(
        @Id
        @Column(name = "tid", nullable = false, precision = 0)
        @GeneratedValue(strategy = GenerationType.AUTO)
        var tid: Int = 0,
        @Basic
        @Column(name = "sid", nullable = false, precision = 0)
        var sid: Int = 0,
        @Basic
        @Column(name = "title", nullable = false, length = 45)
        var title: String = "",
        @Basic
        @Column(name = "lastReplyTime", nullable = true)
        var lastReplyTime: Timestamp = Timestamp(0),
        @Basic
        @Column(name = "hasBestAnswer", nullable = true)
        var hasBestAnswer: Boolean = false,
        @Basic
        @Column(name = "postTime", nullable = false)
        var postTime: Timestamp = Timestamp(0),
        @ManyToOne
        @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false)
        var userByUid: UserEntity? = null,
        @ManyToOne
        @JoinColumn(name = "lastReplyUid", referencedColumnName = "uid", nullable = false)
        var userByLastReplyUid: UserEntity? = null,
        @OneToMany
        var replies: List<ReplyEntity>? = null

)