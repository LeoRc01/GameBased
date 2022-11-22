package com.cip.cipstudio.model

import android.util.Log
import com.cip.cipstudio.utils.AuthErrorEnum
import com.cip.cipstudio.view.widgets.LoadingSpinner
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.userProfileChangeRequest

object User {
    private val TAG = User::class.java.simpleName

    var logged = false
    var uid: String? = null
    var email: String? = null
    var username: String? = null
    var photoUrl: String? = null
    private val auth = FirebaseAuth.getInstance()

    init {
        initUser()
    }

    fun initUser() {
        if (isUserLogged()) {
            uid = auth.currentUser!!.uid
            email = auth.currentUser!!.email
            username = auth.currentUser!!.displayName
            photoUrl = auth.currentUser!!.photoUrl.toString()
            logged = true
        }
    }

    fun login(email: String,
              password: String) : Task<AuthResult> {
        val task = auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                initUser()
            }
        return task
    }

    fun register(email: String,
                 password: String,
                 username: String) : Task<AuthResult> {
        val task = auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val usernameUpdates = userProfileChangeRequest { displayName = username }
                auth.currentUser!!.updateProfile(usernameUpdates)
                    .addOnSuccessListener {
                        Log.i(TAG, "Username registered successfully")
                    }
            }
        return task
    }

    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }

}