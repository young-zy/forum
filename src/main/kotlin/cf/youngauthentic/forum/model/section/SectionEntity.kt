package cf.youngauthentic.forum.model.section

import cf.youngauthentic.forum.model.thread.ThreadEntity
import javax.persistence.*

@Entity
@Table(name = "section", schema = "Forum")
data class SectionEntity(
        @Id
        @Column(name = "sid", nullable = false)
        var sid: Int = 0,
        @Basic
        @Column(name = "section_name", nullable = true, length = 45)
        var sectionName: String? = null,
        @Column(nullable = true)
        @OneToMany(cascade = [CascadeType.ALL])
        var threads: List<ThreadEntity>? = null
)