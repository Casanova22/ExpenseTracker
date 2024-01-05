package com.ceej.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ceej.expensetracker.databinding.FragmentSignupBinding

class FragmentSignUp : Fragment(), View.OnClickListener {

    private var _signUpBinding : FragmentSignupBinding? = null
    private val signUpBinding get() = _signUpBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _signUpBinding = FragmentSignupBinding.inflate(inflater, container, false)

        onClickSignUp()
        return signUpBinding.root
    }

    override fun onClick(v: View?) {
        when(v) {
            signUpBinding.buttonConfirmSignup -> findNavController().navigate(R.id.action_fragmenSignup_to_fragmentLogin)
        }
    }

    private fun onClickSignUp() {
        signUpBinding.buttonConfirmSignup.setOnClickListener(this)
    }
}