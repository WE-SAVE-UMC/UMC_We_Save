package com.example.we_save

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.we_save.databinding.ActivitySearchBinding

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
    }

    private fun MoveFragment() {
        val fragment = SearchDescriptionFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_contatiner_view, fragment)
            .addToBackStack(null) // 뒤로 가기 가능
            .commit()
    }
}