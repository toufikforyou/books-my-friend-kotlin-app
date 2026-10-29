// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // AGP 9 compiles Kotlin itself; declaring the plugin here only pins a newer Kotlin compiler.
    alias(libs.plugins.kotlin.android) apply false
}
