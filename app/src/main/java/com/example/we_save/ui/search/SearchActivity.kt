package com.example.we_save.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.we_save.R
import com.example.we_save.data.apiservice.RetrofitClient
import com.example.we_save.databinding.ActivitySearchBinding
import com.example.we_save.ui.MainActivity
import retrofit2.Call
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchButton: ImageButton = binding.searchButtonIv
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        searchButton.setOnClickListener {
            val query = binding.searchEdittextTv.text.toString()
            if (query.isNotEmpty()) {
                fetchTags(query)
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.searchEdittextTv.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {

                // Enter 키가 눌릴 때 줄바꿈이 일어나지 않도록 이벤트 소비
                if (event?.action == KeyEvent.ACTION_DOWN) {
                    val query = binding.searchEdittextTv.text.toString()
                    if (query.isNotEmpty()) {
                        fetchTags(query)
                    } else {
                        Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
                    }
                    return@setOnEditorActionListener true // 이벤트를 소비하여 줄바꿈 방지
                }
            }
            return@setOnEditorActionListener false

        }

        binding.arrowIv.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        supportPostponeEnterTransition()
        binding.searchEdittextTv.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                binding.searchEdittextTv.viewTreeObserver.removeOnPreDrawListener(this)
                supportStartPostponedEnterTransition()
                return true
            }
        })
    }

    private fun fetchTags(query: String) {
        val call = RetrofitClient.homeSearchService.searchTags(query)
        call.enqueue(object : retrofit2.Callback<SearchResponse> {
            override fun onResponse(
                call: Call<SearchResponse>,
                response: Response<SearchResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { searchResponse ->
                        if (searchResponse.isSuccess) {
                            val tags = searchResponse.result.tags
                            val countermeasures = searchResponse.result.countermeasureDtos
                            if (tags.isNotEmpty()) {
                                showResults(tags, countermeasures)
                                binding.wrongSearchResultTv.visibility = View.GONE
                            } else {
                                binding.wrongSearchResultTv.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@SearchActivity, "서버 오류: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Toast.makeText(this@SearchActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showResults(tags: List<String>, countermeasures: List<CountermeasureDto>) {

        val tagsAdapter = TagAdapter(tags)
        binding.keyowrdButton.visibility=View.VISIBLE
        binding.tagsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = tagsAdapter
        }
        val fragment = SearchDescriptionFragment.newInstance(ArrayList(countermeasures), ArrayList(tags))
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_contatiner_view, fragment)
            .addToBackStack(null)
            .commit()
    }
}