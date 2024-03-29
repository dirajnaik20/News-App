package com.example.androidlearning.presentation.viewmodel


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.androidlearning.data.model.APIResponse
import com.example.androidlearning.data.model.Article
import com.example.androidlearning.data.util.Resource
import com.example.androidlearning.domain.usecase.DeleteSavedNewsUseCase
import com.example.androidlearning.domain.usecase.GetNewsHeadlinesUseCase
import com.example.androidlearning.domain.usecase.GetSavedNewsUseCase
import com.example.androidlearning.domain.usecase.GetSearchedNewsUseCase
import com.example.androidlearning.domain.usecase.SaveNewsUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.PrivateKey

class NewsViewModel(
    private val app: Application,
    private val getNewsHeadlinesUseCase: GetNewsHeadlinesUseCase,
    private val getSearchedNewsUseCase: GetSearchedNewsUseCase,
    private val saveNewsUseCase: SaveNewsUseCase,
    private val getSavedNewsUseCase: GetSavedNewsUseCase,
    private val deleteSavedNewsUseCase: DeleteSavedNewsUseCase
) : AndroidViewModel(app) {
    val newHeadLines = MutableLiveData<Resource<APIResponse>>()

    fun getNewsHeadlines(country: String, page: Int) = viewModelScope.launch(Dispatchers.IO) {

        newHeadLines.postValue(Resource.Loading())

        try {
            if (isInternetAvailable(app)) {
                val apiResult = getNewsHeadlinesUseCase.execute(country, page)
                newHeadLines.postValue(apiResult)
            } else {
                newHeadLines.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (e: Exception) {
            newHeadLines.postValue(Resource.Error(e.message.toString()))
        }


    }

    //search News
    val searchedNews = MutableLiveData<Resource<APIResponse>>()

    fun searchNews(
        country: String,
        searchQuery: String,
        page: Int
    ) = viewModelScope.launch {
        searchedNews.postValue(Resource.Loading())

        try {
            if (isInternetAvailable(app)) {
                val response = getSearchedNewsUseCase.execute(
                    country,
                    searchQuery,
                    page
                )

                searchedNews.postValue(response)
            } else {
                searchedNews.postValue(Resource.Error("No internet connection"))
            }
        } catch (e: Exception) {
            searchedNews.postValue(Resource.Error(e.message.toString()))
        }

    }

    @Suppress("DEPRECATION")
    fun isInternetAvailable(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }

    fun saveNews(article: Article)=viewModelScope.launch {
        saveNewsUseCase.execute(article)
    }

    fun getSavedNews()= liveData{
        getSavedNewsUseCase.execute().collect{
            emit(it)
        }

    }

    fun deleteArticle(article: Article)=viewModelScope.launch {
        deleteSavedNewsUseCase.execute(article)
    }
}