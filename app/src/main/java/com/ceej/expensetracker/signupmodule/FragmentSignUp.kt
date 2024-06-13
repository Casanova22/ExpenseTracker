package com.ceej.expensetracker.signupmodule

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.ceej.expensetracker.R
import com.ceej.expensetracker.databinding.FragmentSignupBinding
import com.ceej.expensetracker.utils.isConnectionAvailable
import com.ceej.expensetracker.utils.ValidationUtils
import com.ceej.expensetracker.utils.ValidationUtils.validateConfirmPassword
import com.ceej.expensetracker.utils.ValidationUtils.validateEmail
import com.ceej.expensetracker.utils.ValidationUtils.validateUserName
import com.ceej.expensetracker.viewmodel.SignInViewModel


class FragmentSignUp : Fragment() {

    private lateinit var appViewModel: SignInViewModel

    private var _signUpBinding: FragmentSignupBinding? = null
    private val signUpBinding get() = _signUpBinding!!

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _signUpBinding = FragmentSignupBinding.inflate(inflater, container, false)
        appViewModel = ViewModelProvider(this)[SignInViewModel::class.java]


        checkInternetConnection()
        observeViewModel()
        setUpListeners()
        setClickableText(signUpBinding.eulaPrivacy)
        return signUpBinding.root
    }

    private fun checkInternetConnection() {
        if (!requireContext().isConnectionAvailable()) {
            Toast.makeText(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDataToFireStore(username: String, email: String, password: String) {
        signUpBinding.progressBar.visibility = View.VISIBLE
        appViewModel.saveUserDataToFireStore(username, email, password)
    }

    private fun observeViewModel() {
        appViewModel.fireStoreResult.observe(viewLifecycleOwner) { result  ->
            signUpBinding.progressBar.visibility = View.GONE
            when (result) {
                is SignInViewModel.FireStoreResult.Success -> {
                    Toast.makeText(requireContext(), "User data saved successfully!", Toast.LENGTH_SHORT).show()
                }
                is SignInViewModel.FireStoreResult.Failure -> {
                    Toast.makeText(requireContext(), "Firestore Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpListeners() {
        with(signUpBinding) {
            etUserName.addTextChangedListener(TextFieldValidation(::validateUserName))
            etEmail.addTextChangedListener(TextFieldValidation(::validateEmail))
            etPassword.addTextChangedListener(TextFieldValidation(::validatePassword))
            etpassConfirm.addTextChangedListener(TextFieldValidation(::validateConfirmPassword))

            buttonConfirmSignup.setOnClickListener {
                if (validateUserName() && validateEmail() && validatePassword() && validateConfirmPassword()) {
                    val username = etUserName.text.toString().trim()
                    val email = etEmail.text.toString().trim()
                    val password = etPassword.text.toString().trim()
                    saveUserDataToFireStore(username, email, password)
                    it.findNavController().navigate(R.id.action_fragmentSignup_to_fragmentLogin)
                    Toast.makeText(requireContext(), getString(R.string.sign_up_successful), Toast.LENGTH_SHORT).show()
                } else {
                    horizontalShake(it, SHAKE_DISTANCE)
                    Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //-------------Field Validations-------------//
    private fun validateUserName(): Boolean {
        val username = signUpBinding.etUserName.text.toString().trim()
        val validUsername = validateUserName(username)
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.text_color_secondary)
        val colorNoState = ContextCompat.getColor(requireContext(), R.color.no_state)
        val colorWeak = ContextCompat.getColor(requireContext(), R.color.weak_password)
        return if (username.isEmpty()) {
            signUpBinding.userNameCont.error = null
            signUpBinding.pwValidator.validUsername.setCardBackgroundColor(ColorStateList.valueOf(colorNoState))
            false
        } else if (!validateUserName(username)) {
            signUpBinding.userNameCont.error = getString(R.string.invalid_username_length)
            false
        } else {
            signUpBinding.userNameCont.error = null
            signUpBinding.pwValidator.validUsername.setCardBackgroundColor(
                ColorStateList.valueOf(if (validUsername) colorStrong else colorWeak)
            )
            true
        }
    }

    private fun validateEmail() : Boolean {
        val email = signUpBinding.etEmail.text.toString()
        return if (email.isEmpty()) {
            signUpBinding.emailCont.error = null
            false
        } else if (!validateEmail(email)) {
            signUpBinding.emailCont.error = getString(R.string.invalid_email)
            false
        } else {
            signUpBinding.emailCont.error = null
            true
        }
    }

    private fun validatePassword() : Boolean {
        val password = signUpBinding.etPassword.text.toString()
        val isValidLength = ValidationUtils.passwordLength(password)
        val isValidRegex = ValidationUtils.validatePasswordRegex(password)
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.text_color_secondary)
        val colorWeak = ContextCompat.getColor(requireContext(), R.color.weak_password)
        val colorNoState = ContextCompat.getColor(requireContext(), R.color.no_state)

        if (password.isEmpty()) {
            signUpBinding.passwordCont.error = null
            return false
        } else {
            signUpBinding.passwordCont.error = if (!isValidLength) {
                getString(R.string.password_is_weak)
            } else {
                null
            }
            signUpBinding.pwValidator.passwordLength.setCardBackgroundColor(
                ColorStateList.valueOf(if (isValidLength) colorStrong else colorWeak)
            )
        }

        val validations = listOf(
            Pair(password.contains("[A-Z]".toRegex()), signUpBinding.pwValidator.passwordUppercase),
            Pair(password.contains("[0-9]".toRegex()), signUpBinding.pwValidator.passwordNumerical),
            Pair(password.contains("[!.@#\$%^&*()_+{}\\|:\"<>?]".toRegex()), signUpBinding.pwValidator.passwordSpecial)
        )

        validations.forEach {
            setValidationState(it.second, it.first, colorStrong, colorNoState)
        }
        return isValidLength && isValidRegex
    }

    private fun validateConfirmPassword() : Boolean {
        val password = signUpBinding.etPassword.text.toString()
        val confirmPassword = signUpBinding.etpassConfirm.text.toString()
        return if (confirmPassword.isEmpty()) {
            signUpBinding.confirmPassCont.error = null
            false
        } else if (!validateConfirmPassword(password, confirmPassword)) {
            signUpBinding.confirmPassCont.error = getString(R.string.passwords_don_t_match)
            false
        } else {
            signUpBinding.confirmPassCont.error = null
            true
        }
    }

    private fun setValidationState(view: CardView, isValid: Boolean, validColor: Int, noStateColor: Int) {


        view.setCardBackgroundColor(ColorStateList.valueOf(if (isValid) validColor else noStateColor))
    }

    inner class TextFieldValidation(private val validator: () -> Boolean) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validator()
        }
    }

    //------------PRIVACY POLICY & EULA CLICK LOGIC-------------------//
    private fun setClickableText(textView: TextView) {
        val eulaText = getString(R.string.eula_privacy)
        val spannableString = SpannableString(eulaText)

        val eulaClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // EULA click logic
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.text_color_secondary)
                ds.bgColor = Color.TRANSPARENT
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Privacy Policy click logic
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.text_color_secondary)
                ds.bgColor = Color.TRANSPARENT
            }
        }

        spannableString.setSpan(eulaClickableSpan, eulaText.indexOf(getString(R.string.eula)), eulaText.indexOf(getString(R.string.eula)) + getString(R.string.eula).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacyClickableSpan, eulaText.indexOf(getString(R.string.privacy_policy)), eulaText.indexOf(getString(R.string.privacy_policy)) + getString(R.string.privacy_policy).length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
        textView.highlightColor = Color.TRANSPARENT
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun openWebView(webView: WebView, url: String) {
        webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }
            }
            loadUrl(url)
        }
    }

    private fun horizontalShake(view: View, shakeDistance: Float) {
        ObjectAnimator.ofFloat(view, "translationX", 0f, shakeDistance, -shakeDistance, 0f).apply {
            duration = 300
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _signUpBinding = null
        appViewModel.fireStoreResult.removeObservers(viewLifecycleOwner)
    }

    companion object {
        private const val TAG = "FragmentSignUp"
        private const val PASSWORD_MIN_LENGTH = 10
        private const val USERNAME_MIN_LENGTH = 8
        private const val USERNAME_MAX_LENGTH = 10
        private const val SHAKE_DISTANCE = 20F
    }
}
