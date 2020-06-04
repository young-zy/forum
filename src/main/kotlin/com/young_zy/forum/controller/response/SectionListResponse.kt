package com.young_zy.forum.controller.response

import com.young_zy.forum.model.section.SectionEntity

data class SectionListResponse(
        val sections: List<SectionEntity>
) : Response()