package com.example.we_save.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.we_save.R
import com.example.we_save.databinding.ActivitySearchBinding
import com.example.we_save.ui.MainActivity

class SearchActivity : AppCompatActivity() {
    lateinit var binding : ActivitySearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val searchButton: ImageButton = binding.searchButtonIv
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        searchButton.setOnClickListener {
            MoveFragment()
        }
        binding.arrowIv.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun MoveFragment() {
        val fragment = SearchDescriptionFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_contatiner_view, fragment)
            .addToBackStack(null) // 뒤로 가기 가능
            .commit()
    }
}