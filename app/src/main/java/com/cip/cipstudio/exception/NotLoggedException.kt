package com.cip.cipstudio.exception

class NotLoggedException : Exception() {
    override val message: String?
        get() = "User not logged"
}
