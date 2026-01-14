package com.dicoding.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
