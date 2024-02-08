package com.ceej.expensetracker.signupmodule

import android.content.Context.CONNECTIVITY_SERVICE
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.ceej.expensetracker.R
import com.ceej.expensetracker.databinding.FragmentSignupBinding
import com.ceej.expensetracker.signupmodule.FieldValidators.isStringContainNumber
import com.ceej.expensetracker.signupmodule.FieldValidators.isStringContainSpecialCharacter
import com.ceej.expensetracker.signupmodule.FieldValidators.isStringLowerAndUpperCase
import com.ceej.expensetracker.signupmodule.FieldValidators.isValidEmail
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class FragmentSignUp : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var _signUpBinding : FragmentSignupBinding? = null
    private val signUpBinding get() = _signUpBinding!!


    private val isConnectionAvailable: Boolean
        get() {
            val cm = requireContext().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(network)
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = cm.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnectedOrConnecting
            }
        }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _signUpBinding = FragmentSignupBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        FirebaseApp.initializeApp(requireContext())
        setUpListeners()
        return signUpBinding.root
    }

    private fun isValidate(): Boolean =
        validateUserName() && validateEmail() && validatePassword() && validateConfirmPassword()

    private fun setUpListeners () {

        signUpBinding.etUserName.addTextChangedListener(TextFieldValidation(signUpBinding.etUserName))
        signUpBinding.etEmail.addTextChangedListener(TextFieldValidation(signUpBinding.etEmail))
        signUpBinding.etPassword.addTextChangedListener(TextFieldValidation(signUpBinding.etPassword))
        signUpBinding.etpassConfirm.addTextChangedListener(TextFieldValidation(signUpBinding.etpassConfirm))
    }

    private fun validateUserName(): Boolean {

        if (signUpBinding.etUserName.text.toString().trim().isEmpty()) {
            signUpBinding.userNameCont.error = "Required*"
            signUpBinding.etUserName.requestFocus()
            return false
        } else {
            signUpBinding.userNameCont.error = null
        }
        return true

        /*if (signUpBinding.etUserName.text.toString().trim().isEmpty()) {
            signUpBinding.userNameCont.error = null
            signUpBinding.etUserName.requestFocus()
        } else if (signUpBinding.etUserName.text.){}*/
    }

    private fun validateEmail(): Boolean {

        if (signUpBinding.etEmail.text.toString().trim().isEmpty()) {
            signUpBinding.emailCont.error = "Required*"
            signUpBinding.etEmail.requestFocus()
            return false
        } else if (!isValidEmail(signUpBinding.etEmail.text.toString())) {
            signUpBinding.emailCont.error = "Invalid email"
            signUpBinding.etEmail.requestFocus()
            return false
        } else {
            signUpBinding.emailCont.error = null
        }
        return true
    }

    private fun validatePassword(): Boolean {

        val validatedColor = ContextCompat.getColor(requireContext(),R.color.strong_password)
        val invalidatedColor = ContextCompat.getColor(requireContext(), R.color.black)

        if (signUpBinding.etPassword.text.toString().trim().isEmpty()) {
            signUpBinding.passwordCont.error = "Required*"
            signUpBinding.etPassword.requestFocus()
            return false
        } else if (signUpBinding.etPassword.text?.length!! >= 8) {
            signUpBinding.passwordCont.error = null
        }


        if (isStringLowerAndUpperCase(signUpBinding.etPassword.text.toString())) {
            signUpBinding.pwValidator.passwordUppercase.setTextColor(validatedColor)
            signUpBinding.etPassword.requestFocus()
            return false

        } else if (signUpBinding.etPassword.text.toString().isBlank()){
            signUpBinding.pwValidator.passwordUppercase.setTextColor(invalidatedColor)
            signUpBinding.etPassword.requestFocus()
            return false
        } else {
            signUpBinding.pwValidator.passwordUppercase.setTextColor(Color.BLACK)

        }

        if (isStringContainSpecialCharacter(signUpBinding.etPassword.text.toString())) {
            signUpBinding.pwValidator.passwordSpecial.setTextColor(validatedColor)
            signUpBinding.etPassword.requestFocus()
            return false
        } else {
            signUpBinding.pwValidator.passwordSpecial.setTextColor(invalidatedColor)
        }

        if (isStringContainNumber(signUpBinding.etPassword.text.toString())) {
            signUpBinding.pwValidator.passwordNumerical.setTextColor(validatedColor)
            signUpBinding.etPassword.requestFocus()
            return false
        } else {
            signUpBinding.pwValidator.passwordNumerical.setTextColor(invalidatedColor)
            signUpBinding.pwValidator.passwordNumerical.drawableState
        }
        return true
    }

    private fun validateConfirmPassword(): Boolean {

        val color = ContextCompat.getColor(requireContext(),R.color.strong_password)

        when {
            signUpBinding.etpassConfirm.text.toString().trim().isEmpty() -> {
                signUpBinding.confirmPassCont.error = "Required*"
                signUpBinding.etpassConfirm.requestFocus()
                return false
            }
            signUpBinding.etpassConfirm.text.toString() != signUpBinding.etPassword.text.toString() -> {
                signUpBinding.confirmPassCont.error = "Passwords doesn't match"
            }
            else -> {
                signUpBinding.confirmPassCont.error = "Passwords Match"
            }
        }
        return true
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.etUserName -> {
                    validateUserName()
                }
                R.id.etEmail -> {
                    validateEmail()
                }
                R.id.etPassword -> {
                    validatePassword()
                }
                R.id.etpassConfirm -> {
                    validateConfirmPassword()
                }
            }
        }
    }
}
