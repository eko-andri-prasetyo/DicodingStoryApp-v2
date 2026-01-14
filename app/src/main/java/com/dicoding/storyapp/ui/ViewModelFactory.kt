package com.dicoding.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.ui.add.AddStoryViewModel
import com.dicoding.storyapp.ui.auth.AuthViewModel
import com.dicoding.storyapp.ui.main.MainViewModel
import com.dicoding.storyapp.ui.maps.MapsViewModel
import com.dicoding.storyapp.ui.welcome.WelcomeViewModel

class ViewModelFactory private constructor(
    private val repository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository) as T
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository) as T
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> AddStoryViewModel(repository) as T
            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> WelcomeViewModel(repository) as T
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> MapsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val repo = Injection.provideRepository(context)
                INSTANCE ?: ViewModelFactory(repo).also { INSTANCE = it }
            }
        }
    }
}
