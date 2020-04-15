package com.young_zy.forum.model.section

import com.young_zy.forum.model.thread.ThreadInListProjection
import org.springframework.data.rest.core.config.Projection

@Projection(types = [SectionEntity::class])
interface SectionWithThreadsProjection {
    val sid: Int
    val sectionId: Int
    val threads: List<ThreadInListProjection>
}