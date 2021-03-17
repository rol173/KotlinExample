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
                println("mapItem: ${mapItem.value.login} newUser: ${newUser.login}")
                if (mapItem.value.login == newUser.login) throw IllegalArgumentException("A user with this email already exists")
            }
            map[newUser.login] = newUser
        }
    }


    fun requestAccessCode(login: String) {
        var correctedLogin = login.trim()
        if (login[0] == '+') correctedLogin = correctedLogin.replace("""[^+\d]""".toRegex(), "")
        map[correctedLogin]?.let {
            val oldPass = it.accessCode
            it.accessCode = it.generateAccessCode()
            it.changePassword(oldPass!!, it.accessCode!!)
        }
    }

    /*fun loginUser(login: String, password: String): String? =
        map[login.also{it.trim().replace("""[^+\d]""".toRegex(), "")}]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }*/

    fun loginUser(login: String, password: String): String? {
        var correctedLogin = login.trim()
        if (login[0] == '+') correctedLogin = correctedLogin.replace("""[^+\d]""".toRegex(), "")
        return map[correctedLogin]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }
    }


    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    /*Реализуй метод importUsers(list: List): List, в качестве аргумента принимает список строк где
    разделителем полей является ";" данные перечислены в следующем порядке -
    Полное имя пользователя; email; соль:хеш пароля; телефон
    (Пример: " John Doe ;JohnDoe@unknow.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;")
    метод должен вернуть коллекцию список User (Пример возвращаемого userInfo:
    firstName: John
    lastName: Doe
    login: johndoe@unknow.com
    fullName: John Doe
    initials: J D
    email: JohnDoe@unknow.com
    phone: null
    meta:
    { src = csv }
    ), при этом meta должно содержать "src" : "csv", если сзначение в csv строке пустое то
    соответствующее свойство в объекте User должно быть null, обратите внимание что salt и hash
    пароля в csv разделены ":" , после импорта пользователей вызов метода loginUser должен отрабатывать
    корректно (достаточно по логину паролю)*/

    fun importUsers(list: List<String>): List<User> {
        var result = mutableListOf<User>()
        var args = listOf<String>()
        for (itemList in list) {
            args = itemList.split(";")
            var fullName = args[0]
            var email = if (args[1].isBlank()) null else args[1]
            var saltAndPassHash = args[2]
            var phone = if (args[3].isBlank()) null else args[3]
            /*val (fullName, email;, saltAndPassHash, phone) =
                itemList.split(";")*/
            User.makeUser2(
                fullName = fullName,
                email = email,
                saltAndPassHash = saltAndPassHash,
                phone = phone
            ).also {
                result.add(it)
            }
        }
        return result
    }
}