package com.dicoding.storyapp.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)
        if (story == null) {
            Toast.makeText(this, "Data story tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.tvDetailName.text = story.name ?: "-"
        binding.tvDetailDescription.text = story.description ?: "-"
        Glide.with(this)
            .load(story.photoUrl)
            .centerCrop()
            .into(binding.ivDetailPhoto)
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
