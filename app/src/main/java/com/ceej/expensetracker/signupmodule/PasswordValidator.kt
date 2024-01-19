package com.ceej.expensetracker.signupmodule

import android.service.autofill.FieldClassification.Match
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import com.ceej.expensetracker.R
import java.util.regex.Matcher
import java.util.regex.Pattern

class PasswordValidator: TextWatcher {

    private var passwordStrength : MutableLiveData<String> = MutableLiveData()
    private var strengthColor : MutableLiveData<Int> = MutableLiveData()

    private var lowerCase: MutableLiveData<Int> = MutableLiveData()
    private var upperCase: MutableLiveData<Int> = MutableLiveData()
    private var digit: MutableLiveData<Int> = MutableLiveData()
    private var special: MutableLiveData<Int> = MutableLiveData()

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s != null){
            lowerCase.value = if (s.hasLowerCase()){1} else{0}
            upperCase.value = if (s.hasUpperCase()){1} else{0}
            digit.value = if (s.hasDigits()){1} else{0}
            special.value = if (s.hasSpecialChar()){1} else{0}
            calculateStrength(s)
        }
    }

    override fun afterTextChanged(s: Editable?) {}

    private fun calculateStrength(password: CharSequence) {
        if (password.length in 0..7) {
            strengthColor.value = R.color.weak_password
            passwordStrength.value = "Weak Password Strength"
        }
        else if (password.length in 8..10 && (lowerCase.value == 1 || upperCase.value == 1 || special.value == 1)) {
            strengthColor.value = R.color.medium_password
            passwordStrength.value = "Medium Password Strength"
        }
        else if (password.length in 11..16 && (lowerCase.value == 1 || upperCase.value == 1 || special.value == 1)) {
            strengthColor.value = R.color.strong_password
            passwordStrength.value = "Strong Password"
        }
        else if (password.length > 16 && lowerCase.value == 1 || upperCase.value == 1 || digit.value == 1 || special.value == 1) {
            strengthColor.value = R.color.bullet
            passwordStrength.value = "Bullet"
        }
    }


    private fun CharSequence.hasLowerCase() : Boolean {
        val pattern: Pattern = Pattern.compile("[a-z]")
        val hasLowerCase : Matcher = pattern.matcher(this)
        return hasLowerCase.find()
    }

    private fun CharSequence.hasUpperCase() : Boolean {
        val pattern : Pattern = Pattern.compile("[A-Z]")
        val hasUpperCase : Matcher = pattern.matcher(this)
        return hasUpperCase.find()
    }

    private fun CharSequence.hasDigits() : Boolean {
        val pattern : Pattern = Pattern.compile("[0-9]")
        val hasDigits : Matcher = pattern.matcher(this)
        return hasDigits.find()
    }

    private fun CharSequence.hasSpecialChar() : Boolean {
        val pattern : Pattern = Pattern.compile("[!@#\$%^&*()_/.+={}/;:'<>?/\\\\[\\\\]\\\\,\\\\~`-]]")
        val hasSpecial : Matcher = pattern.matcher(this)
        return hasSpecial.find()
    }
}