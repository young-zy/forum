package cf.youngauthentic.forum.model

import cf.youngauthentic.forum.model.user.UserEntity
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "Thread", schema = "Forum")
data class ThreadEntity(
        @Id
        @Column(name = "tid", nullable = false, precision = 0)
        private var tid: Int = 0,
        @Basic
        @Column(name = "title", nullable = false, length = 45)
        private var title: String = "",
        @Basic
        @Column(name = "lastReplyTime", nullable = true)
        private var lastReplyTime: Timestamp = Timestamp(0),
        @Basic
        @Column(name = "hasBestAnswer", nullable = true)
        private var hasBestAnswer: Boolean = false,
        @Basic
        @Column(name = "postTime", nullable = false)
        private var postTime: Timestamp = Timestamp(0),
        @ManyToOne
        @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false)
        private var userByUid: UserEntity? = null,
        @ManyToOne
        @JoinColumn(name = "lastReplyUid", referencedColumnName = "uid", nullable = false)
        private var userByLastReplyUid: UserEntity? = null
)