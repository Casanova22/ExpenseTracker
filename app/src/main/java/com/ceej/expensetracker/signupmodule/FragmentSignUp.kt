package com.ceej.expensetracker.signupmodule

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.ceej.expensetracker.R
import com.ceej.expensetracker.databinding.FragmentSignupBinding
import com.ceej.expensetracker.utils.isConnectionAvailable
import com.ceej.expensetracker.signupmodule.FieldValidators.isValidEmail
import com.ceej.expensetracker.viewmodel.SignInViewModel
import com.google.android.material.textfield.TextInputLayout


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

        setUpListeners()
        if (!requireContext().isConnectionAvailable()) {
            Toast.makeText(requireContext(),
                getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
        }
        setClickableText(signUpBinding.eulaPrivacy)
        return signUpBinding.root
    }

    private fun saveUserDataToFireStore(username: String, email: String, password: String) {
        appViewModel.saveUserDataToFireStore(
            username,
            email,
            password
        )

        appViewModel.fireStoreResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SignInViewModel.FireStoreResult.Success -> {
                    val documentId = result.documentId
                    Toast.makeText(requireContext(), "User data saved successfully!", Toast.LENGTH_SHORT).show()
                }
                is SignInViewModel.FireStoreResult.Failure -> {

                    val error = result.error
                    Toast.makeText(requireContext(), "Firestore Error: $error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setUpListeners() {
        with(signUpBinding) {
            etUserName.addTextChangedListener(TextFieldValidation(etUserName))
            etEmail.addTextChangedListener(TextFieldValidation(etEmail))
            etPassword.addTextChangedListener(TextFieldValidation(etPassword))
            etpassConfirm.addTextChangedListener(TextFieldValidation(etpassConfirm))

            buttonConfirmSignup.setOnClickListener {
                val username = etUserName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()

                if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    saveUserDataToFireStore(username, email, password)
                    signUpBinding.buttonConfirmSignup.findNavController().navigate(R.id.action_fragmentSignup_to_fragmentLogin)
                    Toast.makeText(requireContext(),
                        getString(R.string.sign_up_successful), Toast.LENGTH_SHORT).show()
                } else {
                    horizontalShake(buttonConfirmSignup, 20F)
                    Toast.makeText(requireContext(),
                        getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun validateUserName(): Boolean = validateField(
        signUpBinding.userNameCont,
        signUpBinding.etUserName.text.toString(),
        { it.length in 8..10 },
        getString(R.string.invalid_username_length),
        getString(R.string.valid_username)
    )

    private fun validateEmail(): Boolean = validateField(
        signUpBinding.emailCont,
        signUpBinding.etEmail.text.toString(),
        ::isValidEmail,
        getString(R.string.invalid_email),
        getString(R.string.valid_email)
    )

    private fun validatePasswordLength(): Boolean {
        val password = signUpBinding.etPassword.text.toString()
        val colorWeak = ContextCompat.getColor(requireContext(), R.color.weak_password)
        val colorError = ContextCompat.getColor(requireContext(), R.color.text_color_secondary)
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.strong_password)
        val colorNoState = ContextCompat.getColor(requireContext(), R.color.no_state)
        val check: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.baseline_check_circle_24, requireContext().theme)
        val error: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.error, requireContext().theme)

        return if (password.isEmpty() || password.length <= 10) {
            setErrorState(signUpBinding.passwordCont,
                getString(R.string.password_is_weak), colorWeak, error)
            signUpBinding.pwValidator.passwordLength.setCardBackgroundColor(ColorStateList.valueOf(colorNoState))
            false
        } else {
            setValidState(signUpBinding.passwordCont,
                getString(R.string.valid_password_length), colorError, check)
            signUpBinding.pwValidator.passwordLength.setCardBackgroundColor(ColorStateList.valueOf(colorStrong))
            true
        }
    }

    private fun validatePasswordTextview(): Boolean {
        val password = signUpBinding.etPassword.text.toString()
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.strong_password)
        val colorNoState = ContextCompat.getColor(requireContext(), R.color.no_state)


        val uppercaseCharRegex = "[A-Z]".toRegex()
        val numericRegex = "[0-9]".toRegex()
        val specialCharRegex = "[!.@#\\\$%^&*()_+{}\\|:\"<>?]".toRegex()

        val isValid = password.contains(uppercaseCharRegex) &&
                password.contains(numericRegex) &&
                password.contains(specialCharRegex)


        setValidationState(signUpBinding.pwValidator.passwordUppercase, password.contains(uppercaseCharRegex), colorStrong, colorNoState)
        setValidationState(signUpBinding.pwValidator.passwordNumerical, password.contains(numericRegex), colorStrong, colorNoState)
        setValidationState(signUpBinding.pwValidator.passwordSpecial, password.contains(specialCharRegex), colorStrong, colorNoState)

        return isValid
    }

    private fun validateConfirmPassword(): Boolean = validateField(
        signUpBinding.confirmPassCont,
        signUpBinding.etpassConfirm.text.toString(),
        { it == signUpBinding.etPassword.text.toString() },
        getString(R.string.passwords_don_t_match),
        getString(R.string.passwords_match)
    )

    private fun validateField(field: TextInputLayout, value: String, validator: (String) -> Boolean, errorMessage: String, validMessage: String): Boolean {
        val colorWeak = ContextCompat.getColor(requireContext(), R.color.weak_password)
        val colorStrong = ContextCompat.getColor(requireContext(), R.color.text_color_secondary)
        val check: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.baseline_check_circle_24, requireContext().theme)
        val error: Drawable? = ResourcesCompat.getDrawable(resources, R.drawable.error, requireContext().theme)

        return if (!validator(value)) {
            setErrorState(field, errorMessage, colorWeak, error)
            false
        } else {
            setValidState(field, validMessage, colorStrong, check)
            true
        }
    }

    private fun setErrorState(textInputLayout: TextInputLayout, errorMessage: String, color: Int, errorIcon: Drawable?) {
        textInputLayout.error = errorMessage
        textInputLayout.setErrorIconTintList(ColorStateList.valueOf(color))
        textInputLayout.errorIconDrawable = errorIcon
        textInputLayout.setErrorTextColor(ColorStateList.valueOf(color))
        textInputLayout.boxStrokeErrorColor = ColorStateList.valueOf(color)
        textInputLayout.requestFocus()
    }

    private fun setValidState(textInputLayout: TextInputLayout, validMessage: String, color: Int, checkIcon: Drawable?) {
        textInputLayout.error = validMessage
        textInputLayout.setErrorIconTintList(ColorStateList.valueOf(color))
        textInputLayout.errorIconDrawable = checkIcon
        textInputLayout.setErrorTextColor(ColorStateList.valueOf(color))
        textInputLayout.boxStrokeErrorColor = ColorStateList.valueOf(color)
    }

    private fun setValidationState(view: CardView, isValid: Boolean, validColor: Int, noStateColor: Int) {
        view.setCardBackgroundColor(ColorStateList.valueOf(if (isValid) validColor else noStateColor))
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (view.id) {
                R.id.etUserName -> validateUserName()
                R.id.etEmail -> validateEmail()
                R.id.etPassword -> {
                    validatePasswordLength()
                    validatePasswordTextview()
                }
                R.id.etpassConfirm -> validateConfirmPassword()
            }
        }
    }

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
            }
        }

        val eulaStart = eulaText.indexOf(getString(R.string.eula))
        val eulaEnd = eulaStart + getString(R.string.eula).length

        val privacyStart = eulaText.indexOf(getString(R.string.privacy_policy))
        val privacyEnd = privacyStart + getString(R.string.privacy_policy).length

        spannableString.setSpan(eulaClickableSpan, eulaStart, eulaEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(privacyClickableSpan, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannableString
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
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, shakeDistance, -shakeDistance, 0f)
        animator.duration = 300
        animator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        signUpBinding.buttonConfirmSignup.setOnClickListener(null)
        _signUpBinding = null
    }

    companion object {
        private const val TAG = "FragmentSignUp"
    }
}
