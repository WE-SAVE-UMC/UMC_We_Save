package com.example.we_save.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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

        val rootLayout = findViewById<View>(R.id.main1)

        val sharedPreferences = getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        val query = sharedPreferences.getString("search_query", "")

        if (!query.isNullOrEmpty()) {
            // EditText에 값 설정
            binding.searchEdittextTv.setText(query)
        }
        binding.searchEdittextTv.requestFocus()
        // 루트 레이아웃에 터치 리스너 추가
        rootLayout.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }

        // EditText를 클릭할 때만 키보드가 뜨도록 하기 위한 설정
        binding.searchEdittextTv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard(binding.searchEdittextTv)
            }
        }

        searchButton.setOnClickListener {
            val query = binding.searchEdittextTv.text.toString()
            if (query.isNotEmpty()) {
                fetchTags(query)
            } else {

            }
        }
        binding.searchEdittextTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.endsWith(" ")) {
                        binding.searchEdittextTv.removeTextChangedListener(this) // 잠시 리스너 제거
                        binding.searchEdittextTv.setText(it.trim()) // 공백 제거
                        binding.searchEdittextTv.setSelection(binding.searchEdittextTv.text.length) // 커서 위치 조정
                        binding.searchEdittextTv.addTextChangedListener(this) // 리스너 다시 추가
                    }
                }
            }
        })
        binding.searchEdittextTv.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.isNotEmpty() && source.last() == ' ') {
                // 공백을 입력하지 않도록 필터링
                return@InputFilter source.trimEnd()
            }
            null
        })
        binding.searchEdittextTv.imeOptions = EditorInfo.IME_ACTION_DONE

        binding.searchEdittextTv.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.searchEdittextTv.text.toString().trim() // 공백 제거
                if (query.isNotEmpty()) {
                    fetchTags(query)
                } else {
                }
                return@setOnEditorActionListener true // 이벤트를 소비하여 줄바꿈 방지
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
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    // 키보드를 보이는 함수
    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    // 이 메서드를 오버라이드하여 뒤로가기 버튼을 눌렀을 때 키보드를 숨기도록 할 수 있습니다.
    override fun onBackPressed() {
        if (currentFocus != null) {
            hideKeyboard()
        } else {
            super.onBackPressed()
        }
    }
}