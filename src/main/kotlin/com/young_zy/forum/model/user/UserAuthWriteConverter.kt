package com.young_zy.forum.model.user

import com.google.gson.Gson
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class UserAuthWriteConverter(private val gson: Gson) : Converter<UserAuth, String> {
    override fun convert(source: UserAuth): String {
        return gson.toJson(source)
    }

}