package com.ceej.expensetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ceej.expensetracker.databinding.FragmentMainBinding

class FragmentMain : Fragment() {

    private var _mainBinding: FragmentMainBinding? = null
    private val mainBinding get() = _mainBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mainBinding = FragmentMainBinding.inflate(inflater, container, false)

        return mainBinding.root
    }
}