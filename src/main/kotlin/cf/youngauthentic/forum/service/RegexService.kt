package cf.youngauthentic.forum.service

import org.springframework.stereotype.Service

@Service
class RegexService {
    //at least one capitalized character, one number, and one uncapitalized character
    val passwordRegex: Regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z!@#\$%^&*\\d]{8,}\$")


    val emailRegex = Regex("(?:[a-z0-9!#\$%&'*+\\/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+\\/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")

    //4-20 characters long _, - are allowed, only a-z or A-Z can be used for first character
    val usernameRegex = Regex("^(?=.{4,20}\$)(?![_0-9])(?!.*[_]{2})[a-zA-Z0-9_-]+(?<![_])\$")

    fun validateUsername(username: String) {
        if (!usernameRegex.matches(username)) {
            throw IllegalArgumentException("username pattern not correct!")
        }
    }

    fun validatePassword(password: String) {
        if (!passwordRegex.matches(password)) {
            throw IllegalArgumentException("password pattern not correct!")
        }
    }

    fun validateEmail(email: String) {
        if (!emailRegex.matches(email)) {
            throw IllegalArgumentException("email pattern not correct!")
        }
    }
}