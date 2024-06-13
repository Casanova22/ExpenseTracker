package com.ceej.expensetracker.utils


import android.util.Patterns

object ValidationUtils {

    fun validateUserName(username: String): Boolean {
        return username.length in 8..10
    }

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun passwordLength(password: String): Boolean {
        return password.length > 10
    }

    fun validatePasswordRegex(password: String): Boolean {
        return password.contains("[A-Z]".toRegex()) &&
                password.contains("[0-9]".toRegex()) &&
                password.contains("[!.@#\$%^&*()_+{}\\|:\"<>?]".toRegex())
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}
