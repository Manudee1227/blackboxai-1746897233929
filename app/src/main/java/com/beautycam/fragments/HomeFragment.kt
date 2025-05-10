package com.beautycam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beautycam.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView for video list
        val recyclerView = view.findViewById<RecyclerView>(R.id.videosRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        // TODO: Set up adapter and load videos
        // For now, we'll just show an empty list
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
