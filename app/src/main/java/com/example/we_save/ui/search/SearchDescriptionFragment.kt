package com.example.we_save.ui.search

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.R
import com.example.we_save.databinding.FragmentSearchDescriptionBinding
import com.example.we_save.ui.MainActivity
import com.example.we_save.ui.main.MainFragment

class SearchDescriptionFragment : Fragment() {
    lateinit var binding: FragmentSearchDescriptionBinding
    private var isDescriptionVisible = false

    private lateinit var countermeasures: List<CountermeasureDto>
    private lateinit var tags: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.descriptionMainTv.text = "'"+tags.joinToString(", ")+"' 응급 처치"
        binding.hospitalRightArrowIv.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("TARGET_PAGE", 1) // 1은 FacilitiesFragment의 포지션
            startActivity(intent)
        }

        binding.countermeasuresRecyclerView.apply {
            layoutManager = object : LinearLayoutManager(context) {
                override fun canScrollVertically(): Boolean {
                    return false // 스크롤 비활성화
                }
            }
            adapter = CountermeasuresAdapter(countermeasures)
        }

    }



    companion object {
        private const val ARG_COUNTERMEASURES = "countermeasures"
        private const val ARG_TAGS = "tags"

        fun newInstance(countermeasures: ArrayList<CountermeasureDto>, tags: ArrayList<String>): SearchDescriptionFragment {
            val fragment = SearchDescriptionFragment()
            val args = Bundle().apply {
                putParcelableArrayList(ARG_COUNTERMEASURES, countermeasures)
                putStringArrayList(ARG_TAGS, tags)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            countermeasures = it.getParcelableArrayList(ARG_COUNTERMEASURES) ?: emptyList()
            tags = it.getStringArrayList(ARG_TAGS) ?: emptyList()
        }
    }
}