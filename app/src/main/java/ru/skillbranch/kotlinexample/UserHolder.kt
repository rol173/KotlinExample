package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

class UserHolder {

    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User = User.makeUser(fullName, email = email, password = password)
        .also { newUser ->
            for (mapItem in map) {
                if (mapItem.value.login == newUser.login) throw IllegalArgumentException("A user with this email already exists")
            }
            map[newUser.login] = newUser
        }


    fun registerUserByPhone(fullName: String, rawPhone: String): User {

        val validPhoneSymbols = "()-0123456789 "
        var phone = ""
        if (rawPhone[0] == '+') {
            phone = rawPhone.substring(1 until rawPhone.length)
            phone.map {
                if (it !in validPhoneSymbols) throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
            }
        }
        return User.makeUser(fullName, phone = rawPhone).also { newUser ->
            for (mapItem in map) {
                if (mapItem.value.login == newUser.login) throw IllegalArgumentException("A user with this email already exists")
            }
            map[newUser.login] = newUser
        }
    }


    fun requestAccessCode(login: String) {
        map[login.trim()]?.let {
            val oldPass = it.accessCode
            it.accessCode = it.generateAccessCode()
            it.changePassword(oldPass!!, it.accessCode!!)
        }
    }

    fun loginUser(login: String, password: String): String? =
        map[login.trim()]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }


    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}