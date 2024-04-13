package com.ceej.expensetracker.signupmodule

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.ceej.expensetracker.R
import com.ceej.expensetracker.databinding.FragmentSignupBinding
import com.ceej.expensetracker.signupmodule.FieldValidators.isValidEmail
import com.google.firebase.firestore.FirebaseFirestore

class FragmentSignUp : Fragment() {

    //private lateinit var auth: FirebaseAuth

    private var _signUpBinding : FragmentSignupBinding? = null
    private val signUpBinding get() = _signUpBinding!!

    val db = FirebaseFirestore.getInstance()

    /*private val isConnectionAvailable: Boolean
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
        }*/

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _signUpBinding = FragmentSignupBinding.inflate(inflater, container, false)
        //auth = Firebase.auth

        //FirebaseApp.initializeApp(requireContext())
        setUpListeners()
        return signUpBinding.root


    }

    private fun saveUserDataToFirestore(username: String, email:String, password:String) {
        val user = hashMapOf(
            "username" to username,
            "email" to email,
            "password" to password
        )

        db.collection("info")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener{
                Log.w(TAG, "Error")
            }

    }

    private fun setUpListeners () {


        signUpBinding.etUserName.addTextChangedListener(TextFieldValidation(signUpBinding.etUserName))
        signUpBinding.etEmail.addTextChangedListener(TextFieldValidation(signUpBinding.etEmail))
        signUpBinding.etPassword.addTextChangedListener(TextFieldValidation(signUpBinding.etPassword))
        signUpBinding.etpassConfirm.addTextChangedListener(TextFieldValidation(signUpBinding.etpassConfirm))

        signUpBinding.buttonConfirmSignup.setOnClickListener {
            val username = signUpBinding.etUserName.text.toString()
            val email = signUpBinding.etEmail.text.toString()
            val password = signUpBinding.etPassword.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){

                saveUserDataToFirestore(username, email, password)
            } else {
                horizontalShake(signUpBinding.buttonConfirmSignup, 20f)
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUserName(username : String): Boolean {
        val username = signUpBinding.etUserName.text.toString()
        val minLength = 8
        val maxLength = 10
        val check : Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.baseline_check_circle_24, requireContext().theme)
        val error : Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.error, requireContext().theme)
        val colorWeak = ContextCompat.getColor(requireContext(), R.color.weak_password)
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.strong_password)

        return when {

            username.isEmpty() -> {
                signUpBinding.userNameCont.error = "Required*"
                signUpBinding.userNameCont.setErrorIconTintList(ColorStateList.valueOf(colorWeak))
                signUpBinding.userNameCont.errorIconDrawable = error
                signUpBinding.userNameCont.setErrorTextColor(ColorStateList.valueOf(colorWeak))
                signUpBinding.userNameCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)
                signUpBinding.etUserName.requestFocus()
                false
            }

            username.length < minLength -> {
                signUpBinding.userNameCont.error = "Invalid username length"
                signUpBinding.userNameCont.setErrorIconTintList(ColorStateList.valueOf(colorWeak))
                signUpBinding.userNameCont.errorIconDrawable = error
                signUpBinding.userNameCont.setErrorTextColor(ColorStateList.valueOf(colorWeak))
                signUpBinding.userNameCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)
                false
            }

            username.length > maxLength -> {
                signUpBinding.userNameCont.error = "Maximum $maxLength characters"
                signUpBinding.userNameCont.setErrorIconTintList(ColorStateList.valueOf(colorWeak))
                signUpBinding.userNameCont.errorIconDrawable = error
                signUpBinding.userNameCont.setErrorTextColor(ColorStateList.valueOf(colorWeak))
                signUpBinding.userNameCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)
                false
            }

            else -> {
                signUpBinding.userNameCont.error = "Valid username"
                signUpBinding.userNameCont.setErrorIconTintList(ColorStateList.valueOf(colorStrong))
                signUpBinding.userNameCont.errorIconDrawable = check
                signUpBinding.userNameCont.setErrorTextColor(ColorStateList.valueOf(colorStrong))
                signUpBinding.userNameCont.boxStrokeErrorColor = ColorStateList.valueOf(colorStrong)
                true
            }
        }
    }

    private fun validateEmail(): Boolean {

        val colorWeak = ContextCompat.getColor(requireContext(), R.color.weak_password)
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.strong_password)
        val check : Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.baseline_check_circle_24, requireContext().theme)
        val error : Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.error, requireContext().theme)

        if (signUpBinding.etEmail.text.toString().trim().isEmpty()) {
            signUpBinding.emailCont.error = "Required*"
            signUpBinding.emailCont.setErrorIconTintList(ColorStateList.valueOf(colorWeak))
            signUpBinding.emailCont.errorIconDrawable = error
            signUpBinding.emailCont.setErrorTextColor(ColorStateList.valueOf(colorWeak))
            signUpBinding.emailCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)
            signUpBinding.etEmail.requestFocus()
            return false

        } else if (!isValidEmail(signUpBinding.etEmail.text.toString())) {
            signUpBinding.emailCont.error = "Invalid email"
            signUpBinding.emailCont.setErrorIconTintList(ColorStateList.valueOf(colorWeak))
            signUpBinding.emailCont.errorIconDrawable = error
            signUpBinding.emailCont.setErrorTextColor(ColorStateList.valueOf(colorWeak))
            signUpBinding.emailCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)
            signUpBinding.etEmail.requestFocus()
            return false

        } else {
            signUpBinding.emailCont.error = "Valid email"
            signUpBinding.emailCont.setErrorIconTintList(ColorStateList.valueOf(colorStrong))
            signUpBinding.emailCont.errorIconDrawable = check
            signUpBinding.emailCont.setErrorTextColor(ColorStateList.valueOf(colorStrong))
            signUpBinding.emailCont.boxStrokeErrorColor = ColorStateList.valueOf(colorStrong)
        }
        return true
    }

    private fun validatePasswordLength(): Boolean {
        val password = signUpBinding.etPassword.text.toString()
        val colorWeak = ContextCompat.getColor(requireContext(), R.color.weak_password)
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.strong_password)
        val colorNoState = ContextCompat.getColor(requireContext(), R.color.no_state)
        val check : Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.baseline_check_circle_24, requireContext().theme)
        val error : Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.error, requireContext().theme)


        return when {

            password.isEmpty() -> {
                signUpBinding.passwordCont.error = "Required*"
                signUpBinding.passwordCont.setErrorIconTintList(ColorStateList.valueOf(colorWeak))
                signUpBinding.passwordCont.errorIconDrawable = error
                signUpBinding.passwordCont.setErrorTextColor(ColorStateList.valueOf(colorWeak))
                signUpBinding.passwordCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)

                false
            }

            password.length <= 8 -> {
                signUpBinding.passwordCont.error = "Password is weak"
                signUpBinding.passwordCont.setErrorIconTintList(ColorStateList.valueOf(colorWeak))
                signUpBinding.passwordCont.errorIconDrawable = error
                signUpBinding.passwordCont.setErrorTextColor(ColorStateList.valueOf(colorWeak))
                signUpBinding.passwordCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)
                signUpBinding.pwValidator.passwordLength.setCardBackgroundColor(ColorStateList.valueOf(colorNoState))
                false
            }

            else -> {
                password.length >= 10
                signUpBinding.passwordCont.error = "Valid password length"
                signUpBinding.passwordCont.setErrorIconTintList(ColorStateList.valueOf(colorStrong))
                signUpBinding.passwordCont.errorIconDrawable = check
                signUpBinding.passwordCont.setErrorTextColor(ColorStateList.valueOf(colorStrong))
                signUpBinding.passwordCont.boxStrokeErrorColor = ColorStateList.valueOf(colorStrong)
                signUpBinding.pwValidator.passwordLength.setCardBackgroundColor(ColorStateList.valueOf(colorStrong))
                true
            }
        }
    }

    private fun validatePasswordTextview() : Boolean {
        val password = signUpBinding.etPassword.text.toString()
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.strong_password)
        val colorNoState = ContextCompat.getColor(requireContext(), R.color.no_state)

        val uppercaseCharRegex = "[A-Z]".toRegex()
        val numericRegex = "[0-9]".toRegex()
        val specialCharRegex = "[!.@#\\\$%^&*()_+{}\\|:\"<>?]".toRegex()

        // Check if password contains at least one uppercase letter
        if (!password.contains(uppercaseCharRegex)) {
            signUpBinding.pwValidator.passwordUppercase.setCardBackgroundColor(ColorStateList.valueOf(colorNoState))
            return false
        }

        // Check if password contains at least one numeric digit
        if (!password.contains(numericRegex)) {
            signUpBinding.pwValidator.passwordNumerical.setCardBackgroundColor(ColorStateList.valueOf(colorNoState))
            return false
        }

        // Check if password contains at least one special character
        if (!password.contains(specialCharRegex)) {
            signUpBinding.pwValidator.passwordSpecial.setCardBackgroundColor(ColorStateList.valueOf(colorNoState))
            return false
        }

        // All conditions met
        signUpBinding.pwValidator.passwordUppercase.setCardBackgroundColor(ColorStateList.valueOf(colorStrong))
        signUpBinding.pwValidator.passwordNumerical.setCardBackgroundColor(ColorStateList.valueOf(colorStrong))
        signUpBinding.pwValidator.passwordSpecial.setCardBackgroundColor(ColorStateList.valueOf(colorStrong))
        return true
    }

    private fun validateConfirmPassword(): Boolean {
        val colorWeak = ContextCompat.getColor(requireContext(), R.color.weak_password)
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.strong_password)
        val check : Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.baseline_check_circle_24, requireContext().theme)

        when {

            signUpBinding.etpassConfirm.text.toString().trim().isEmpty() -> {
                signUpBinding.confirmPassCont.error = "Required*"
                signUpBinding.confirmPassCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)
                signUpBinding.etpassConfirm.requestFocus()
                return false
            }

            signUpBinding.etpassConfirm.text.toString() != signUpBinding.etPassword.text.toString() -> {
                signUpBinding.confirmPassCont.error = "Passwords doesn't match"
                signUpBinding.confirmPassCont.setErrorTextColor(ColorStateList.valueOf(colorWeak))
                signUpBinding.confirmPassCont.boxStrokeErrorColor = ColorStateList.valueOf(colorWeak)
                return false
            }

            else -> {
                signUpBinding.confirmPassCont.error = "Passwords Match"
                signUpBinding.confirmPassCont.errorIconDrawable = check
                signUpBinding.confirmPassCont.setErrorIconTintList(ColorStateList.valueOf(colorStrong))
                signUpBinding.confirmPassCont.setErrorTextColor(ColorStateList.valueOf(colorStrong))
                signUpBinding.confirmPassCont.boxStrokeErrorColor = ColorStateList.valueOf(colorStrong)
                signUpBinding.confirmPassCont.requestFocus()
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
                    validateUserName("")
                }
                R.id.etEmail -> {
                    validateEmail()
                }
                R.id.etPassword -> {
                    validatePasswordLength()
                    validatePasswordTextview()
                }
                R.id.etpassConfirm -> {
                    validateConfirmPassword()
                }
            }
        }
    }

    private fun horizontalShake(
        view: View,
        offset: Float,
        repeatCount: Int = 3,
        dampingRatio: Float? = null,
        duration: Long = 350L,
        interpolator: Interpolator = AccelerateInterpolator()
    ) {
        val defaultDampingRatio = dampingRatio ?: (1f / (repeatCount + 1))
        val animValues = mutableListOf<Float>()
        repeat(repeatCount) { index ->
            animValues.add(0f)
            animValues.add(-offset * (1 - defaultDampingRatio * index))
            animValues.add(0f)
            animValues.add(offset * (1 - defaultDampingRatio * index))
        }
        animValues.add(0f)

        val anim : ValueAnimator = ValueAnimator.ofFloat(*animValues.toFloatArray())
        anim.addUpdateListener {
            view.translationX = it.animatedValue as Float
        }
        anim.interpolator = interpolator
        anim.duration = duration
        anim.start()
    }

    companion object {
        private const val TAG = "FragmentSignUp"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _signUpBinding = null
    }
}