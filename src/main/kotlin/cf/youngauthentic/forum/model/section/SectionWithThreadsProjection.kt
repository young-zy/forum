package cf.youngauthentic.forum.model.section

import cf.youngauthentic.forum.model.thread.ThreadInListProjection
import org.springframework.data.rest.core.config.Projection

@Projection(types = [SectionEntity::class])
interface SectionWithThreadsProjection {
    val sid: Int
    val sectionId: Int
    val threads: List<ThreadInListProjection>
}