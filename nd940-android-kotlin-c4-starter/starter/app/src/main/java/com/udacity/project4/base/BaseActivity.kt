package com.udacity.project4.base

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.authentication.AuthenticationActivity

open class BaseActivity: AppCompatActivity() {
    protected val authFirebase: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    protected fun navigateAuthentication() {
        startActivity(Intent(this, AuthenticationActivity::class.java))
    }
}