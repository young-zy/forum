package com.young_zy.forum.controller.response

import com.young_zy.forum.model.thread.ThreadProjection

data class HitRateResponse(
        var list: List<ThreadProjection>
) : Response()