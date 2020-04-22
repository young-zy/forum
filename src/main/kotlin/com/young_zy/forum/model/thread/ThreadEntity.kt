package com.young_zy.forum.model.thread

import com.young_zy.forum.model.user.UserEntity
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "thread", schema = "Forum")
data class ThreadEntity(
        @Id
        @Column(name = "tid", nullable = false, precision = 0)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var tid: Int = 0,
        @Basic
        @Column(name = "sid", nullable = false, precision = 0)
        var sid: Int = 0,
        @Basic
        @Column(name = "uid", nullable = false)
        var uid: Int = 0,
        @Basic
        @Column(name = "title", nullable = false, length = 45)
        var title: String = "",
        @Basic
        @Column(name = "lastReplyTime", nullable = true)
        var lastReplyTime: Timestamp = Timestamp(System.currentTimeMillis()),
        @Basic
        @Column(name = "lastReplyUid", nullable = true)
        var lastReplyUid: Int = 0,
        @Basic
        @Column(name = "hasBestAnswer", nullable = true)
        var hasBestAnswer: Boolean = false,
        @Basic
        @Column(name = "postTime", nullable = false)
        var postTime: Timestamp = Timestamp(System.currentTimeMillis()),
        @Basic
        @Column(name = "question", nullable = false)
        var question: Boolean,
        @ManyToOne
        @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false, insertable = false, updatable = false)
        var author: UserEntity? = null,
        @ManyToOne
        @JoinColumn(name = "lastReplyUid", referencedColumnName = "uid", nullable = false, insertable = false, updatable = false)
        var userByLastReplyUid: UserEntity? = null
)