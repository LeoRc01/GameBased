package com.cip.cipstudio.utils

import android.content.Context
import com.cip.cipstudio.R

enum class AuthErrorEnum {
    EMAIL_NOT_VALID {
        override fun getErrorMessage(context: Context) = context.getString(R.string.email_not_valid)
        override fun getErrorType() = AuthTypeErrorEnum.EMAIL
                    },
    EMAIL_ALREADY_IN_USE {
        override fun getErrorMessage(context: Context) = context.getString(R.string.email_already_in_use)
        override fun getErrorType() = AuthTypeErrorEnum.EMAIL
                    },
    EMAIL_NOT_REGISTERED {
        override fun getErrorMessage(context: Context) = context.getString(R.string.email_not_registered)
        override fun getErrorType() = AuthTypeErrorEnum.EMAIL
                    },
    PASSWORD_NOT_CORRECT {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_not_correct)
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_TOO_SHORT {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_too_short)
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_TOO_LONG {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_too_long)
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NOT_VALID {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_not_valid)
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NO_DIGIT {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_no_digit)
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NO_UPPERCASE {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_no_uppercase)
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NO_LOWERCASE {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_no_lowercase)
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORD_NO_SPECIAL_CHARACTER {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_no_special_character)
        override fun getErrorType() = AuthTypeErrorEnum.PASSWORD
                    },
    PASSWORDS_NOT_MATCH {
        override fun getErrorMessage(context: Context) = context.getString(R.string.passwords_not_match)
        override fun getErrorType() = AuthTypeErrorEnum.CONFIRM_PASSWORD
                    },
    UNKNOWN_ERROR {
        override fun getErrorMessage(context: Context) = context.getString(R.string.unknown_error)
        override fun getErrorType() = AuthTypeErrorEnum.UNKNOWN
                    };


    abstract fun getErrorMessage(context: Context) : String
    abstract fun getErrorType() : AuthTypeErrorEnum
}