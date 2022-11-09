package com.lx.project5

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lx.project5.databinding.FragmentFirstBinding
import com.lx.project5.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    var _binding: FragmentMyPageBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)

        binding.updateButton.setOnClickListener {
            val curActivity = activity as MainActivity
            curActivity.onFragmentChanged(MainActivity.ScreenItem.ITEMupdate)
        }

        binding.goDogList.setOnClickListener {

        }
        return binding.root
    }

}