package com.lx.project5

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lx.project5.databinding.FragmentAddDogBinding
import com.lx.project5.databinding.FragmentCareMainBinding
import com.lx.project5.databinding.FragmentCareTodolistBinding
import com.lx.project5.databinding.FragmentFirstBinding

class CareTodolistFragment : Fragment() {
    var _binding: FragmentCareTodolistBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCareTodolistBinding.inflate(inflater, container, false)

        binding.backButton2.setOnClickListener {
            (activity as MainActivity).onFragmentChanged(MainActivity.ScreenItem.ITEMcareMain)
        }

        return binding.root
    }


}