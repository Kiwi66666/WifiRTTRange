package com.example.wifirtt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wifirtt.R
import com.example.wifirtt.databinding.FragmentAskForMethodBinding
import com.example.wifirtt.databinding.FragmentBoundingBoxBinding

class BoundingBoxFragment : Fragment() {

    private lateinit var binding: FragmentBoundingBoxBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoundingBoxBinding.inflate(inflater, container, false)
        return binding.root
    }
}