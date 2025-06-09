package com.example.wifirtt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wifirtt.databinding.FragmentRouterListBinding
import com.example.wifirtt.ui.data.ScanRTTRouters

/**
 * A fragment representing a list of Items.
 */
class RouterFragment : Fragment() {
    private lateinit var binding: FragmentRouterListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouterListBinding.inflate(inflater, container, false)
        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = MyRouterRecyclerViewAdapter(ScanRTTRouters.getListForRTT())
        }
        return binding.root
    }
}