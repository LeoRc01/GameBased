package com.cip.cipstudio.utils

import android.util.Patterns

class Validator {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidPassword(password: String): AuthErrorEnum? {
            return when {
                password.length < 8 -> AuthErrorEnum.PASSWORD_TOO_SHORT
                password.length > 20 -> AuthErrorEnum.PASSWORD_TOO_LONG
                !password.matches(Regex(".*\\d.*")) -> AuthErrorEnum.PASSWORD_NO_DIGIT
                !password.matches(Regex(".*[a-z].*")) -> AuthErrorEnum.PASSWORD_NO_LOWERCASE
                !password.matches(Regex(".*[A-Z].*")) -> AuthErrorEnum.PASSWORD_NO_UPPERCASE
                !password.matches(Regex(".*[!@#\$%^&*()_+].*")) -> AuthErrorEnum.PASSWORD_NO_SPECIAL_CHARACTER
                else -> null
            }
        }

        fun isValidLoginPassword(password: String) : Boolean {
            val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#${'$'}%!\-_?&])(?=\S+${'$'}).{8,20}${'$'}""".toRegex()
            return PASSWORD_REGEX.matches(password)
        }

        fun isValidUsername(username: String): AuthErrorEnum? {
            return when {
                username.length < 3 -> AuthErrorEnum.USERNAME_TOO_SHORT
                username.length > 20 -> AuthErrorEnum.USERNAME_TOO_LONG
                !username.matches(Regex("^([_.]*[a-zA-Z0-9][a-zA-Z0-9_.]*)\$")) -> AuthErrorEnum.USERNAME_NOT_VALID
                else -> null
            }
        }
    }


}