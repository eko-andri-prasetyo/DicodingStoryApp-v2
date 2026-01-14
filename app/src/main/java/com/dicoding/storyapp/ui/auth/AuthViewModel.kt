package com.dicoding.storyapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.model.UserModel
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: StoryRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)

    fun login(email: String, password: String) =
        repository.login(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}
