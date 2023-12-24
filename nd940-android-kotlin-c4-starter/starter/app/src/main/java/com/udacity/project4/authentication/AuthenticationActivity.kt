package com.udacity.project4.authentication

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.util.Log
import com.firebase.ui.auth.AuthUI
import android.view.View.OnClickListener
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.base.BaseActivity
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.databinding.ActivityAuthenticationBinding

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : BaseActivity(), OnClickListener {

    private lateinit var _binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        _binding.btnLogin.setOnClickListener(this)
        if (authFirebase.currentUser != null) {
            startActivity(Intent(this, RemindersActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (authFirebase.currentUser != null) {
            startActivity(Intent(this, RemindersActivity::class.java))
        }
    }


    override fun onClick(v: View?) {
        if (v?.id == _binding.btnLogin.id) {
            startActivityForResult()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                startActivity(Intent(this, RemindersActivity::class.java))
                finish()
            }
        }
    }

    private fun startActivityForResult() {
        startActivityForResult(
            AuthUI
                .getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    listOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build()
                    )
                ).build(),
            1
        )
    }
}