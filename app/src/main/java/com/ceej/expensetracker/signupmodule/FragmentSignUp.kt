package com.ceej.expensetracker.signupmodule

import android.content.Context.CONNECTIVITY_SERVICE
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ceej.expensetracker.R
import com.ceej.expensetracker.databinding.FragmentSignupBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.type.Color
import kotlin.math.sign

class FragmentSignUp : Fragment() {

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


        FirebaseApp.initializeApp(requireContext())
        signUpImpl(savedInstanceState)
        configureEditTexts()
        return signUpBinding.root
    }
    private fun signUpImpl(savedInstanceState: Bundle?) {
        val needConnection = resources.getBoolean(R.bool.forConnection)
        val isConnected: Boolean = if (needConnection) {
            isConnectionAvailable
        } else {
            true
        }
        if (isConnected) {
            if (savedInstanceState === null){

            }
        }
    }

    private fun configureEditTexts(){
        signUpBinding.etUserName.setOnFocusChangeListener { _,hasFocus ->
            if (hasFocus) {
                signUpBinding.userNameCont.hint = getText(R.string.userHint)
                signUpBinding.userNameCont.defaultHintTextColor = ContextCompat.getColorStateList(requireContext(), R.color.text_color_primary)
            } else {
                signUpBinding.userNameCont.hint = getText(R.string.required)
                signUpBinding.userNameCont.defaultHintTextColor = ContextCompat.getColorStateList(requireContext(), R.color.holo_required)
            }
        }

        signUpBinding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signUpBinding.emailCont.hint = getText(R.string.emailHint)
                signUpBinding.emailCont.hintTextColor = ContextCompat.getColorStateList(requireContext(), R.color.text_color_primary)
            } else {

                signUpBinding.emailCont.hint = getText(R.string.required)
                signUpBinding.emailCont.defaultHintTextColor = ContextCompat.getColorStateList(requireContext(), R.color.holo_required)
            }
        }

        signUpBinding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signUpBinding.passwordCont.hint = getText(R.string.pwHint)
                signUpBinding.passwordCont.hintTextColor = ContextCompat.getColorStateList(requireContext(), R.color.text_color_primary)
            } else {
                signUpBinding.passwordCont.hint = getText(R.string.required)
                signUpBinding.passwordCont.defaultHintTextColor = ContextCompat.getColorStateList(requireContext(), R.color.holo_required)
            }
        }

        signUpBinding.etpassConfirm.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                signUpBinding.confirmPassCont.hint = getText(R.string.confirm_password)
                signUpBinding.confirmPassCont.hintTextColor = ContextCompat.getColorStateList(requireContext(), R.color.text_color_primary)
            } else {
                signUpBinding.confirmPassCont.hint = getText(R.string.required)
                signUpBinding.confirmPassCont.defaultHintTextColor = ContextCompat.getColorStateList(requireContext(), R.color.holo_required)
            }
        }
    }
}