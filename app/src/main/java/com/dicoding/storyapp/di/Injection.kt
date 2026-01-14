package com.dicoding.storyapp.di

import android.content.Context
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.pref.dataStore
import com.dicoding.storyapp.data.remote.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(context, pref)
        return StoryRepository.getInstance(apiService, pref)
    }
}
