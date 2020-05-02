package com.young_zy.forum.model.reply

import com.young_zy.forum.model.thread.ThreadEntity
import com.young_zy.forum.model.user.UserEntity
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "reply", schema = "Forum")
data class ReplyEntity(
        @Column(name = "rid", nullable = false, precision = 0)
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        override var rid: Int = 0,
        @Column(name = "tid", nullable = false)
        var tid: Int = 0,
        @Column(name = "replyContent", nullable = true)
        @Basic
        override var replyContent: String = "",
        @Column(name = "uid", nullable = true)
        @Basic
        var uid: Int,
        @Column(name = "replyTime", nullable = false)
        @Basic
        override var replyTime: Timestamp = Timestamp(System.currentTimeMillis()),
        @Column(name = "lastEditTime", nullable = false)
        @Basic
        override var lastEditTime: Timestamp = Timestamp(System.currentTimeMillis()),
        @Column(name = "priority", nullable = false, precision = 5, scale = 5)
        @Basic
        override var priority: Double = 0.0,
        @Column(name = "isBestAnswer", nullable = false)
        @Basic
        override var bestAnswer: Boolean = false,
        @Column(name = "upVote", nullable = false)
        @Basic
        var upVote: Int = 0,
        @Column(name = "downVote", nullable = false)
        @Basic
        var downVote: Int = 0,
        @JoinColumn(name = "tid", referencedColumnName = "tid", nullable = false, insertable = false, updatable = false)
        @ManyToOne
        var threadByTid: ThreadEntity? = null,
        @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false, insertable = false, updatable = false)
        @ManyToOne
        override var userByUid: UserEntity? = null
) : ReplyProjection