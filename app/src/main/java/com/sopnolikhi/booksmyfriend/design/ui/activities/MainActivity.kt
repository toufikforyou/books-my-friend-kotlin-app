package com.sopnolikhi.booksmyfriend.design.ui.activities

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.ActivityMainBinding
import com.sopnolikhi.booksmyfriend.design.ui.fragments.ExploreFragment
import com.sopnolikhi.booksmyfriend.design.ui.fragments.HomeFragment
import com.sopnolikhi.booksmyfriend.design.ui.fragments.LibraryFragment
import com.sopnolikhi.booksmyfriend.design.ui.fragments.ProfileFragment

/** Fragments that can jump back to their top when their tab is tapped again. */
interface ScrollableTab {
    fun scrollToTop()
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val tabs = linkedMapOf(
        R.id.navigationHome to "home",
        R.id.navigationExplore to "explore",
        R.id.navigationLibrary to "library",
        R.id.navigationProfile to "profile",
    )

    private val backToHome = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.mainBottomNavigation.selectedItemId = R.id.navigationHome
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                tabs.forEach { (_, tag) -> add(R.id.mainFragmentContainer, createTab(tag), tag) }
            }
        }
        showTab(binding.mainBottomNavigation.selectedItemId)

        binding.mainBottomNavigation.setOnItemSelectedListener { item ->
            showTab(item.itemId)
            true
        }
        binding.mainBottomNavigation.setOnItemReselectedListener { item ->
            (fragmentFor(item.itemId) as? ScrollableTab)?.scrollToTop()
        }
        onBackPressedDispatcher.addCallback(this, backToHome)
    }

    fun openTab(itemId: Int) {
        binding.mainBottomNavigation.selectedItemId = itemId
    }

    /** Jumps to Explore with the keyboard ready for a search. */
    fun openSearch() {
        openTab(R.id.navigationExplore)
        (fragmentFor(R.id.navigationExplore) as? ExploreFragment)?.focusSearch()
    }

    private fun createTab(tag: String): Fragment = when (tag) {
        "explore" -> ExploreFragment()
        "library" -> LibraryFragment()
        "profile" -> ProfileFragment()
        else -> HomeFragment()
    }

    private fun fragmentFor(itemId: Int): Fragment? =
        supportFragmentManager.findFragmentByTag(tabs[itemId])

    private fun showTab(itemId: Int) {
        supportFragmentManager.commitNow {
            tabs.keys.forEach { id ->
                val fragment = fragmentFor(id) ?: return@forEach
                if (id == itemId) show(fragment) else hide(fragment)
            }
        }
        backToHome.isEnabled = itemId != R.id.navigationHome
    }
}
