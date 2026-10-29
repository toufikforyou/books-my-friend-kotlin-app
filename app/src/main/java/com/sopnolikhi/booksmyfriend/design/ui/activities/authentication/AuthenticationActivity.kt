package com.sopnolikhi.booksmyfriend.design.ui.activities.authentication

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.ActivityAuthenticationBinding
import com.sopnolikhi.booksmyfriend.design.ui.activities.MainActivity
import com.sopnolikhi.booksmyfriend.services.includes.extensions.applySystemBarPadding
import com.sopnolikhi.booksmyfriend.services.sessions.LoginSession

/**
 * A gentle first step: the reader only tells us their name. No account is needed until
 * cloud sync exists, so we keep the session on this device.
 */
class AuthenticationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.authRoot.applySystemBarPadding()

        binding.nameInput.doAfterTextChanged { binding.nameInputLayout.error = null }
        binding.nameInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitName()
                true
            } else {
                false
            }
        }
        binding.startReadingButton.setOnClickListener { submitName() }
        binding.skipButton.setOnClickListener { signIn("") }
    }

    private fun submitName() {
        val name = binding.nameInput.text?.toString().orEmpty().trim()
        if (name.length < MIN_NAME_LENGTH) {
            binding.nameInputLayout.error = getString(R.string.nameTooShortText)
            return
        }
        signIn(name)
    }

    private fun signIn(name: String) {
        LoginSession.saveLocalUser(name)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private companion object {
        const val MIN_NAME_LENGTH = 2
    }
}
