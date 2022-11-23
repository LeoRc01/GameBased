package com.cip.cipstudio.utils

import com.cip.cipstudio.R

enum class AuthErrorEnum {
    EMAIL_NOT_VALID {
        override fun getErrorId() = R.string.email_not_valid
        override fun getErrorType() = AuthTypeErrorEnum.EMAIL
                    },
    EMAIL_ALREADY_IN_USE {
        override fun getErrorId() = R.string.email_already_in_use
        override fun getErrorType() = AuthTypeErrorEnum.EMAIL
                    },
    EMAIL_NOT_REGISTERED {
        override fun getErrorId() = R.string.email_not_registered
        override fun getErrorType() = AuthTypeErrorEnum.EMAIL
                    },
    EMAIL_ALREADY_YOURS{
        override fun getErrorId() = R.string.already_your_email
        override fun getErrorType() = AuthTypeErrorEnum.EMAIL
                    },
    PASSWORD_NOT_CORRECT {
        override fun getErrorId() = R.string.password_not_correct
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_TOO_SHORT {
        override fun getErrorId() = R.string.password_too_short
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_TOO_LONG {
        override fun getErrorId() = R.string.password_too_long
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NOT_VALID {
        override fun getErrorId() = R.string.password_not_valid
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NO_DIGIT {
        override fun getErrorId() = R.string.password_no_digit
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NO_UPPERCASE {
        override fun getErrorId() = R.string.password_no_uppercase
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NO_LOWERCASE {
        override fun getErrorId() = R.string.password_no_lowercase
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NO_SPECIAL_CHARACTER {
        override fun getErrorId() = R.string.password_no_special_character
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORDS_NOT_MATCH {
        override fun getErrorId() = R.string.passwords_not_match
        override fun getErrorType() = AuthTypeErrorEnum.CONFIRM_PASSWORD
                    },
    SAME_PASSWORD{
        override fun getErrorId() = R.string.same_password
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    WRONG_PASSWORD{
        override fun getErrorId() = R.string.wrong_password
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    USERNAME_NOT_VALID {
        override fun getErrorId() = R.string.username_not_valid
        override fun getErrorType() = AuthTypeErrorEnum.USERNAME
                    },
    USERNAME_ALREADY_IN_USE {
        override fun getErrorId() = R.string.username_already_in_use
        override fun getErrorType() = AuthTypeErrorEnum.USERNAME
                    },
    USERNAME_TOO_SHORT {
        override fun getErrorId() = R.string.username_too_short
        override fun getErrorType() = AuthTypeErrorEnum.USERNAME
                    },
    USERNAME_TOO_LONG {
        override fun getErrorId() = R.string.username_too_long
        override fun getErrorType() = AuthTypeErrorEnum.USERNAME
                    },
    UNKNOWN_ERROR {
        override fun getErrorId() = R.string.unknown_error
        override fun getErrorType() = AuthTypeErrorEnum.UNKNOWN
                    },
    RECENT_LOGIN_REQUIRED {
        override fun getErrorId() = R.string.recent_login_required
        override fun getErrorType() = AuthTypeErrorEnum.LOGIN
                    },
    ;


    abstract fun getErrorId() : Int
    abstract fun getErrorType() : AuthTypeErrorEnum
}