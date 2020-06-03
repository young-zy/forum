package com.young_zy.forum.controller.response

import com.young_zy.forum.model.section.SectionEntity
import kotlinx.coroutines.flow.Flow

data class SectionListResponse(
        val sections: Flow<SectionEntity>
) : Response()