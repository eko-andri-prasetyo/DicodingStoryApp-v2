package com.dicoding.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.pref.UserPreference
import com.dicoding.storyapp.data.remote.ApiService
import com.dicoding.storyapp.data.remote.response.AddStoryResponse
import com.dicoding.storyapp.data.remote.response.ErrorResponse
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.data.remote.response.LoginResponse
import com.dicoding.storyapp.data.remote.response.RegisterResponse
import com.dicoding.storyapp.data.remote.response.StoryResponse
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository(
    private val apiService: ApiService,
    private val pref: UserPreference
) {

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(parseError(e)))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(parseError(e)))
        }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(location = 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(parseError(e)))
        }
    }

    fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): LiveData<Result<AddStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadStory(file, description, lat, lon)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(parseError(e)))
        }
    }

    suspend fun saveSession(user: UserModel) = pref.saveSession(user)
    fun getSession() = pref.getSession()
    suspend fun logout() = pref.logout()

    private fun parseError(e: Exception): String {
        return try {
            if (e is HttpException) {
                val json = e.response()?.errorBody()?.string()
                val err = Gson().fromJson(json, ErrorResponse::class.java)
                err.message ?: "Terjadi kesalahan (${e.code()})"
            } else {
                e.message ?: "Terjadi kesalahan"
            }
        } catch (ex: Exception) {
            Log.e("StoryRepository", "parseError failed", ex)
            e.message ?: "Terjadi kesalahan"
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, pref: UserPreference): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, pref).also { instance = it }
            }
        }
    }
}
