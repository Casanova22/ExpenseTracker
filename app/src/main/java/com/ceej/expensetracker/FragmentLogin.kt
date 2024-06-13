package com.ceej.expensetracker

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ceej.expensetracker.databinding.FragmentLoginBinding
import com.ceej.expensetracker.signupmodule.FragmentSignUp
import com.ceej.expensetracker.utils.ValidationUtils
import com.ceej.expensetracker.utils.ValidationUtils.validateUserName
import com.ceej.expensetracker.utils.isConnectionAvailable
import com.ceej.expensetracker.viewmodel.SignInViewModel
import org.w3c.dom.Text

class FragmentLogin : Fragment() {

    private lateinit var appViewModel: SignInViewModel

    private var isValid = true
    private var _loginBinding : FragmentLoginBinding? = null
    private val loginBinding get() = _loginBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _loginBinding = FragmentLoginBinding.inflate(inflater, container, false)

        appViewModel = ViewModelProvider(this)[SignInViewModel::class.java]

        loginBinding.signUpTextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentLogin_to_fragmenSignup)
        }

        loginBinding.logInBtn.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentLogin_to_fragmentMain)
        }

        setUpListeners()
        observeViewModel()
        return (loginBinding.root)
    }

    private fun setUpListeners() {
        with(loginBinding) {
            etLogInUserName.addTextChangedListener(TextFieldValidation(etLogInUserName))
            etLogInPassword.addTextChangedListener(TextFieldValidation(etLogInPassword))

            etLogInUserName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val noSpace = s.toString().replace("" , "")
                    if (s.toString() != noSpace) {
                        etLogInUserName.setText(noSpace)
                        etLogInUserName.setSelection(noSpace.length)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            etLogInPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val noSpace = s.toString().replace(" ", "")
                    if (s.toString() != noSpace) {
                        etLogInPassword.setText(noSpace)
                        etLogInPassword.setSelection(noSpace.length)
                    }
                }

            })

            logInBtn.setOnClickListener {
                val username = etLogInUserName.text.toString()
                val password = etLogInPassword.text.toString()

                if (validateInput(username,password)) {

                    if (requireContext().isConnectionAvailable()) {
                        appViewModel.login(username, password)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                    horizontalShake(it, SHAKE_DISTANCE)
                }
            }
        }
    }

    private fun validateInput(username : String, password : String): Boolean {
        if (username.isEmpty()) {
            loginBinding.logInUserNameCont.error = getString(R.string.invalid_username_length)
            isValid = false
        } else {
            loginBinding.logInUserNameCont.error = null
        }

        if (password.isEmpty()) {
            loginBinding.logInPasswordCont.error = getString(R.string.invalid_username_length)
            isValid = false
        } else {
            loginBinding.logInPasswordCont.error = null
        }

        return isValid
    }

    private fun observeViewModel() {
        appViewModel.fireStoreResult.observe(viewLifecycleOwner) { result ->
            when(result){
                is SignInViewModel.FireStoreResult.Success -> {
                    Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_fragmentLogin_to_fragmentMain)
                }
                is SignInViewModel.FireStoreResult.Failure -> {
                    Toast.makeText(requireContext(), "Login Failed: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loginUsernameValidation() {
        val username = loginBinding.etLogInUserName.text.toString().trim()
        if (username.isEmpty() || !validateUserName(username)) {
            loginBinding.logInUserNameCont.error = getString(R.string.invalid_username_length)
        } else {
            loginBinding.logInUserNameCont.error = null
        }
    }

    private fun loginPasswordValidation() {
        val password = loginBinding.etLogInPassword.text.toString().trim()
        if (password.isEmpty() || !validateUserName(password)) {
            loginBinding.logInPasswordCont.error = getString(R.string.valid_password_length)
        } else {
            loginBinding.logInPasswordCont.error = null
        }
    }

    private fun horizontalShake(view: View, shakeDistance: Float) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, shakeDistance, -shakeDistance, 0f)
        animator.duration = 300
        animator.start()
    }

    inner class TextFieldValidation(private val view: View) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when(view.id) {
                R.id.etLogInUserName -> loginUsernameValidation()
                R.id.etLogInPassword -> loginPasswordValidation()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _loginBinding = null
        appViewModel.fireStoreResult.removeObservers(viewLifecycleOwner)
    }

    companion object {
        private const val TAG = "FragmentLogin"
        private const val SHAKE_DISTANCE = 20F
    }
}