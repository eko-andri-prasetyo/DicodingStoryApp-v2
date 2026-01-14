package com.dicoding.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository

class MapsViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStoriesWithLocation() = repository.getStoriesWithLocation()
}
