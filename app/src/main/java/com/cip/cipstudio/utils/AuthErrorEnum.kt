package com.cip.cipstudio.utils

import android.content.Context
import com.cip.cipstudio.R

enum class AuthErrorEnum {
    EMAIL_NOT_VALID {
        override fun getErrorMessage(context: Context) = context.getString(R.string.email_not_valid)
                    },
    EMAIL_ALREADY_IN_USE {
        override fun getErrorMessage(context: Context) = context.getString(R.string.email_already_in_use)
                    },
    EMAIL_NOT_REGISTERED {
        override fun getErrorMessage(context: Context) = context.getString(R.string.email_not_registered)
                    },
    PASSWORD_NOT_CORRECT {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_not_correct)
                    },
    PASSWORD_TOO_SHORT {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_too_short)
                    },
    PASSWORD_TOO_LONG {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_too_long)
                    },
    PASSWORD_NOT_VALID {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_not_valid)
                    },
    PASSWORD_NO_DIGIT {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_no_digit)
                    },
    PASSWORD_NO_UPPERCASE {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_no_uppercase)
                    },
    PASSWORD_NO_LOWERCASE {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_no_lowercase)
                    },
    PASSWORD_NO_SPECIAL_CHARACTER {
        override fun getErrorMessage(context: Context) = context.getString(R.string.password_no_special_character)
                    },
    PASSWORDS_NOT_MATCH {
        override fun getErrorMessage(context: Context) = context.getString(R.string.passwords_not_match)
                    },
    UNKNOWN_ERROR {
        override fun getErrorMessage(context: Context) = context.getString(R.string.unknown_error)
                    };



    abstract fun getErrorMessage(context: Context) : String
}