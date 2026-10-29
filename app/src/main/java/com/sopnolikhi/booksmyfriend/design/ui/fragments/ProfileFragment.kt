package com.sopnolikhi.booksmyfriend.design.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sopnolikhi.booksmyfriend.BuildConfig
import com.sopnolikhi.booksmyfriend.R
import com.sopnolikhi.booksmyfriend.databinding.FragmentProfileBinding
import com.sopnolikhi.booksmyfriend.design.ui.activities.ScrollableTab
import com.sopnolikhi.booksmyfriend.design.ui.activities.authentication.AuthenticationActivity
import com.sopnolikhi.booksmyfriend.services.includes.extensions.applySystemBarPadding
import com.sopnolikhi.booksmyfriend.services.includes.extensions.initial
import com.sopnolikhi.booksmyfriend.services.sessions.LoginSession
import com.sopnolikhi.booksmyfriend.services.stores.LibraryStore
import com.sopnolikhi.booksmyfriend.services.stores.SettingsStore

class ProfileFragment : Fragment(), ScrollableTab {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.profileScroll.applySystemBarPadding(bottom = false)
        setupTheme()
        setupLanguage()
        setupTextSize()

        binding.aboutBody.text = listOf(
            getString(R.string.appNameText),
            getString(R.string.versionFormat, BuildConfig.VERSION_NAME),
        ).joinToString(" · ")
        binding.signOutButton.setOnClickListener { confirmSignOut() }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) refresh()
    }

    private fun refresh() {
        val binding = _binding ?: return
        val name = LoginSession.getUserName().ifBlank { getString(R.string.defaultReaderName) }
        binding.profileName.text = name
        binding.profileAvatar.text = name.initial()
        binding.profileStats.text = getString(
            R.string.profileStatsFormat,
            LibraryStore.inProgressBooks().size,
            LibraryStore.savedBooks().size,
            LibraryStore.finishedCount(),
        )
    }

    private fun setupTheme() {
        val buttonForMode = mapOf(
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to R.id.themeSystemButton,
            AppCompatDelegate.MODE_NIGHT_NO to R.id.themeLightButton,
            AppCompatDelegate.MODE_NIGHT_YES to R.id.themeNightButton,
        )
        binding.themeToggle.check(buttonForMode[SettingsStore.nightMode] ?: R.id.themeSystemButton)
        binding.themeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val mode = buttonForMode.entries.first { it.value == checkedId }.key
            if (mode != SettingsStore.nightMode) SettingsStore.nightMode = mode
        }

        binding.dynamicColorSwitch.isVisible = DynamicColors.isDynamicColorAvailable()
        binding.dynamicColorSwitch.isChecked = SettingsStore.useDynamicColor
        binding.dynamicColorSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked == SettingsStore.useDynamicColor) return@setOnCheckedChangeListener
            SettingsStore.useDynamicColor = isChecked
            activity?.recreate()
        }
    }

    private fun setupLanguage() {
        val current = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        binding.languageToggle.check(
            when {
                current.startsWith("bn") -> R.id.languageBanglaButton
                current.startsWith("en") -> R.id.languageEnglishButton
                else -> R.id.languageSystemButton
            }
        )
        binding.languageToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val locales = when (checkedId) {
                R.id.languageBanglaButton -> LocaleListCompat.forLanguageTags("bn")
                R.id.languageEnglishButton -> LocaleListCompat.forLanguageTags("en")
                else -> LocaleListCompat.getEmptyLocaleList()
            }
            if (locales.toLanguageTags() != AppCompatDelegate.getApplicationLocales().toLanguageTags()) {
                AppCompatDelegate.setApplicationLocales(locales)
            }
        }
    }

    private fun setupTextSize() {
        binding.textSizeSlider.value = SettingsStore.readerFontSize.coerceIn(
            binding.textSizeSlider.valueFrom, binding.textSizeSlider.valueTo
        )
        binding.textSizePreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingsStore.readerFontSize)
        binding.textSizeSlider.addOnChangeListener { _, value, fromUser ->
            if (!fromUser) return@addOnChangeListener
            SettingsStore.readerFontSize = value
            binding.textSizePreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        }
    }

    private fun confirmSignOut() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.signOutConfirmTitle)
            .setMessage(R.string.signOutConfirmBody)
            .setNegativeButton(R.string.cancelText, null)
            .setPositiveButton(R.string.signOutText) { _, _ ->
                LoginSession.logout()
                LibraryStore.clear()
                startActivity(
                    Intent(requireContext(), AuthenticationActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
            .show()
    }

    override fun scrollToTop() {
        _binding?.profileScroll?.smoothScrollTo(0, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
