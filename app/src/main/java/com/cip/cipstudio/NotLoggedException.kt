package com.cip.cipstudio

class NotLoggedException : Exception() {
    override val message: String?
        get() = "User not logged"
}
