package com.example.wifirtt.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.wifirtt.R
import com.example.wifirtt.databinding.FragmentAskForPermitionBinding


class AskForPermitionFragment : Fragment() {
    private lateinit var binding: FragmentAskForPermitionBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAskForPermitionBinding.inflate(inflater, container, false)
        binding.buttonOk.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
            {

                val navController=Navigation.findNavController(requireView())
                navController.navigate(R.id.action_askForPermitionFragment_to_routersFileFragment)
            }
            else {
                activity?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                    )
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
        {

            val navController=Navigation.findNavController(requireView())
            navController.navigate(R.id.action_askForPermitionFragment_to_routersFileFragment)
        }
        super.onViewCreated(view, savedInstanceState)
    }

}