package com.ceej.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ceej.expensetracker.databinding.FragmentLoginBinding

class FragmentLogin : Fragment(), View.OnClickListener {

    private var _firstBinding : FragmentLoginBinding? = null
    private val firstBinding get() = _firstBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _firstBinding = FragmentLoginBinding.inflate(inflater, container, false)

        onClickMain()
        return (firstBinding.root)
    }










    override fun onClick(v: View?) {
        when(v){
            firstBinding.signUpBtn -> findNavController().navigate(R.id.action_fragmentLogin_to_fragmenSignup)
            firstBinding.logInBtn -> findNavController().navigate(R.id.action_fragmentLogin_to_fragmentMain)
        }
    }

    private fun onClickMain() {
        firstBinding.signUpBtn.setOnClickListener(this)
        firstBinding.logInBtn.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _firstBinding = null
    }
}