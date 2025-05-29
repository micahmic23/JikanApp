package com.micah.jikan

import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button: Button = view.findViewById(R.id.infoBttn)

        button.setOnClickListener {
            val info = InfoFragment ()
            info.show((activity as AppCompatActivity).supportFragmentManager, "info")
        }
    }
}