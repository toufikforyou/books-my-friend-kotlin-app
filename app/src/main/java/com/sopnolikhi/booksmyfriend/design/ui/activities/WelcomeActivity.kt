package com.sopnolikhi.booksmyfriend.design.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.ActivityWelcomeBinding
import com.sopnolikhi.booksmyfriend.design.adapters.viewpager.IntroScreenAdapter
import com.sopnolikhi.booksmyfriend.design.ui.activities.authentication.AuthenticationActivity
import com.sopnolikhi.booksmyfriend.models.viewpager.IntroScreenModel
import com.sopnolikhi.booksmyfriend.services.includes.extensions.applySystemBarPadding
import com.sopnolikhi.booksmyfriend.services.includes.utils.IntroUtils
import com.sopnolikhi.booksmyfriend.services.sessions.LoginSession

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    private val introScreenModelList = listOf(
        IntroScreenModel("📚", R.string.onboardingTitle1, R.string.onboardingBody1),
        IntroScreenModel("🌙", R.string.onboardingTitle2, R.string.onboardingBody2),
        IntroScreenModel("🔖", R.string.onboardingTitle3, R.string.onboardingBody3),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        /** Returning readers skip the intro and go straight to their books */
        if (IntroUtils.clearApplicationNone(applicationContext)) {
            openNext()
            return
        }

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.welcomeRoot.applySystemBarPadding()

        val viewPager = binding.introViewPager
        viewPager.adapter = IntroScreenAdapter(introScreenModelList)
        TabLayoutMediator(binding.introTabLayout, viewPager) { _, _ -> }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val isLast = position == introScreenModelList.lastIndex
                binding.introNextButton.setText(if (isLast) R.string.getStartedText else R.string.nextText)
                binding.introSkipButton.animate().alpha(if (isLast) 0f else 1f).start()
                binding.introSkipButton.isEnabled = !isLast
            }
        })

        binding.introNextButton.setOnClickListener {
            if (viewPager.currentItem < introScreenModelList.lastIndex) {
                viewPager.currentItem += 1
            } else {
                finishIntro()
            }
        }
        binding.introSkipButton.setOnClickListener { finishIntro() }
    }

    private fun finishIntro() {
        IntroUtils.savePreviousData(applicationContext)
        openNext()
    }

    private fun openNext() {
        val next = if (LoginSession.isLoggedIn()) MainActivity::class.java else AuthenticationActivity::class.java
        startActivity(Intent(this, next))
        finish()
    }
}
