package com.dicoding.storyapp.ui.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.model.UserModel

class WelcomeViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()
}
