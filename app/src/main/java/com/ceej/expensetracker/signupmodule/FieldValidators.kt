package com.ceej.expensetracker.signupmodule


import android.util.Log
import android.util.Patterns
import java.util.regex.Pattern

object FieldValidators {
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /*fun isStringContainNumber(text: String): Boolean {
        val pattern = Pattern.compile(".*[0-9].*")
        val matcher = pattern.matcher(text)
        return matcher.find()
    }

    fun isStringLowerAndUpperCase(text: String): Boolean {
        val upperCasePattern = Pattern.compile(".*[A-Z].*")
        val upperCasePatterMatcher = upperCasePattern.matcher(text)
        return upperCasePatterMatcher.find()
    }

    fun isStringContainSpecialCharacter(text: String): Boolean {
        val specialCharacterPattern = Pattern.compile(".*[!@#\$%^&*(),.?\":{}|<>].*")
        val specialCharacterMatcher = specialCharacterPattern.matcher(text)
        return specialCharacterMatcher.find()
    }*/
}
