package cf.youngauthentic.forum.model.vote

import javax.persistence.*

@Entity
@Table(name = "vote", schema = "Forum")
@IdClass(VoteEntityPK::class)
data class VoteEntity(
        @Id
        @Column(name = "uid", nullable = false)
        var uid: Int,
        @Id
        @Column(name = "rid", nullable = false, precision = 0)
        var rid: Int,
        @Basic
        @Column(name = "vote", nullable = false)
        var vote: Int
)