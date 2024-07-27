package com.example.we_save

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.databinding.FragmentMainDistanceBinding

class MainDistanceFragment  : Fragment() {
    private var itemdatas = ArrayList<Item>()
    private lateinit var binding: FragmentMainDistanceBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainDistanceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemdatas = arrayListOf(
            Item(R.drawable.earthquake_iv, "지진"),
            Item(R.drawable.fire_iv, "화재"),
            Item(R.drawable.fire_empty_iv, "화재")
        )

        val MainRVadapter = MainRecyclerAdapter(itemdatas)
        binding.nearAccidentRecycler.adapter = MainRVadapter
        binding.nearAccidentRecycler.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)
    }
}