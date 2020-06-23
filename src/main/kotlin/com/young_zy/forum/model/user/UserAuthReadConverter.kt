package com.young_zy.forum.model.user

import com.google.gson.Gson
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class UserAuthReadConverter(private val gson: Gson) : Converter<String, UserAuth> {
    override fun convert(source: String): UserAuth {
        return gson.fromJson(source, UserAuth::class.java)
    }
}