package com.sopnolikhi.booksmyfriend.design.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.ActivityWelcomeBinding
import com.sopnolikhi.booksmyfriend.design.adapters.viewpager.IntroScreenAdapter
import com.sopnolikhi.booksmyfriend.design.ui.activities.authentication.AuthenticationActivity
import com.sopnolikhi.booksmyfriend.models.viewpager.IntroScreenModel
import com.sopnolikhi.booksmyfriend.services.includes.utils.Constants.SLEEP_LOOP_TIME
import com.sopnolikhi.booksmyfriend.services.includes.utils.Constants.SLIDER_LOOP_TIME
import com.sopnolikhi.booksmyfriend.services.includes.utils.IntroUtils
import com.sopnolikhi.booksmyfriend.services.permissions.Locations
import com.sopnolikhi.booksmyfriend.services.permissions.Notification
import com.sopnolikhi.booksmyfriend.services.permissions.PermissionCallback
import com.sopnolikhi.booksmyfriend.services.sessions.LocationSession
import com.sopnolikhi.booksmyfriend.services.sessions.LoginSession

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    private var getStartedBtnAnimation: Animation? = null

    private var handler: Handler? = null
    private var sliderRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** Initialize LoginSession */
        LoginSession.initialize(applicationContext)

        LoginSession.saveLoginUserInfo("username", "456", "example_token", "2024-01-15 12:00:00")

        /** If location permission is granted then fetch location */
        if (Locations.hasLocationPermissions(applicationContext)) {
            LocationSession.displayLocation(applicationContext)
        }

        /** Check Intro Activity is opened previously then login or already logged in check */
        if (IntroUtils.clearApplicationNone(applicationContext)) {
            /** Check if the user is logged in */
            if (LoginSession.isLoggedIn()) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            } else {
                startActivity(Intent(applicationContext, AuthenticationActivity::class.java))
            }
            finish()
            return
        }

        /** If location permission isn't granted then request the location permission */
        if (!Locations.hasLocationPermissions(applicationContext)) {
            Locations.requestLocationPermissions(this)
        }


        val introScreenModelList = mutableListOf(
            IntroScreenModel(
                "Books Slider",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever",
                "https://www.vernier.com/wp-content/uploads/2020/01/LabBuilderwebsite.png"
            ),
            IntroScreenModel(
                "Get notified!",
                "Stay on top of your trip when better offers or personalized suggestions are found",
                "https://static.thenounproject.com/png/4777246-200.png"
            ),
            IntroScreenModel(
                "Nearby shire",
                "শাসনতন্ত্রে বা আ\u200Cইনে প্রদত্ত মৌলিক অধিকার লঙ্ঘনের ক্ষেত্রে উপযুক্ত জাতীয় বিচার আদালতের কাছ থেকে কার্যকর প্রতিকার লাভের অধিকার প্রত্যেকের\u200Cই রয়েছে। কা\u200Cউকে\u200Cই খেয়ালখুশীমত গ্রেপ্তার বা অন্তরীণ করা কিংবা নির্বাসন দে\u200Cওয়া যাবে না।",
                "https://www.vernier.com/wp-content/uploads/2020/01/LabBuilderwebsite.png"
            ),
            IntroScreenModel(
                "Storage",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever",
                "https://i.fbcd.co/products/original/ae2d64e634f5beaa6f0e867d529ece28f0504e9e24fc4d5e0d6fd21f0a05df7f.jpg"
            ),
            IntroScreenModel(
                "Nearby shire",
                "Lorem Ipsum has been the industry's standard dummy text ever",
                "https://i.fbcd.co/products/original/ae2d64e634f5beaa6f0e867d529ece28f0504e9e24fc4d5e0d6fd21f0a05df7f.jpg"
            ),
            // Add more IntroScreenModel items as needed
        )


        val tabLayout: TabLayout = binding.introTabLayout // Replace with your actual ViewPager2 ID
        val viewPager: ViewPager2 = binding.introViewPager


        val introViewPagerAdapter = IntroScreenAdapter(this, introScreenModelList)
        viewPager.adapter = introViewPagerAdapter

        // Assuming you have a TabLayout in your layout with the ID tabLayout
        TabLayoutMediator(tabLayout, viewPager) { _ /*tab*/, _ /*position*/ -> }.attach()

        binding.introNextButton.setOnClickListener {
            viewPager.currentItem =
                (viewPager.currentItem + 1).coerceAtMost(introScreenModelList.lastIndex)
        }

        // Automatic slider after 3 seconds
        handler = Handler(Looper.getMainLooper())
        var sliderTime = SLIDER_LOOP_TIME
        sliderRunnable = object : Runnable {
            override fun run() {
                if (viewPager.currentItem != introScreenModelList.lastIndex) {
                    viewPager.currentItem = (viewPager.currentItem + 1) % introScreenModelList.size
                    sliderTime = SLIDER_LOOP_TIME
                } else {
                    sliderTime += 600000
                }
                handler!!.postDelayed(this, sliderTime.toLong())
            }
        }

        /** On tab selected and click listener */
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    1 -> getNotificationPermission()
                    introScreenModelList.lastIndex -> getStartButtonShow()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab!!.position == introScreenModelList.lastIndex) {
                    getStartButtonHide()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val animButton = binding.animButton

        getStartedBtnAnimation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.animation_swipe_down_to_up)


        animButton.setText(resources.getString(R.string.getStartedText))

        animButton.setOnClickListener {
            Handler().postDelayed({
                IntroUtils.savePreviousData(applicationContext)
                startActivity(Intent(applicationContext, AuthenticationActivity::class.java))
                finish()
            }, SLEEP_LOOP_TIME.toLong())
        }

    }

    override fun onResume() {
        super.onResume()
        // Start the automatic slider when the activity is created or resumed
        handler?.postDelayed(sliderRunnable!!, SLIDER_LOOP_TIME.toLong())
    }

    override fun onPause() {
        // Stop the automatic slider when the activity is paused
        handler?.removeCallbacks(sliderRunnable!!)
        super.onPause()
    }

    override fun onDestroy() {
        // Stop the automatic slider when the activity is destroyed
        handler?.removeCallbacks(sliderRunnable!!)
        super.onDestroy()
    }

    private fun getStartButtonHide() {
        binding.animButton.visibility = View.GONE
        binding.introNextButton.visibility = View.VISIBLE
        binding.introTabLayout.visibility = View.VISIBLE

        // SetUp Animation for this button
        binding.introNextButton.startAnimation(getStartedBtnAnimation)
        binding.introTabLayout.startAnimation(getStartedBtnAnimation)
    }

    private fun getStartButtonShow() {
        binding.animButton.visibility = View.VISIBLE
        binding.introNextButton.visibility = View.GONE
        binding.introTabLayout.visibility = View.GONE

        // SetUp Animation for this button
        binding.animButton.startAnimation(getStartedBtnAnimation)
    }

    fun getNotificationPermission() {
        if (!Notification.hasNotificationPermissions(applicationContext)) {
            Notification.requestNotificationPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Locations.onRequestPermissionsResult(
            requestCode,
            grantResults,
            object : PermissionCallback {
                override fun onPermissionGranted() {
                    LocationSession.displayLocation(applicationContext)
                }

                override fun onPermissionDenied() {
                    Locations.handlePermissionResult(
                        this@WelcomeActivity,
                        grantResults,
                        object : PermissionCallback {
                            override fun onPermissionGranted() {
                                LocationSession.displayLocation(applicationContext)
                            }

                            override fun onPermissionDenied() {
                                Toast.makeText(
                                    applicationContext,
                                    "Location permission is required to get the location",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        })
                }

            })

        Notification.onRequestPermissionsResult(
            requestCode,
            grantResults,
            object : PermissionCallback {
                override fun onPermissionGranted() {}

                override fun onPermissionDenied() {
                    Notification.handlePermissionResult(
                        this@WelcomeActivity,
                        grantResults,
                        object : PermissionCallback {
                            override fun onPermissionGranted() {}

                            override fun onPermissionDenied() {
                                Toast.makeText(
                                    applicationContext,
                                    "Notification Permission Require in this future!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        })
                }

            })
    }
}