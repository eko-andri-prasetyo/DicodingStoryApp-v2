package com.dicoding.storyapp.ui.add

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) = repository.uploadStory(file, description, lat, lon)
}
