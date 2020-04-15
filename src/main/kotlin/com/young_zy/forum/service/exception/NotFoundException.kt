package com.young_zy.forum.service.exception

class NotFoundException(override val message: String = "Requested object not found") : Exception()