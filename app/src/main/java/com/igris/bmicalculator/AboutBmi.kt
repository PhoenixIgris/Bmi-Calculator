package com.igris.bmicalculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igris.bmicalculator.databinding.FragmentAboutBmiBinding


class AboutBmi : Fragment(R.layout.fragment_about_bmi) {

private lateinit var binding: FragmentAboutBmiBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAboutBmiBinding.bind(view)

    }


}