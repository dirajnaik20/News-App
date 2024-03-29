package com.example.androidlearning.domain.usecase

import com.example.androidlearning.data.model.APIResponse
import com.example.androidlearning.data.model.Article
import com.example.androidlearning.data.util.Resource
import com.example.androidlearning.domain.repository.NewsRepository

class DeleteSavedNewsUseCase(private val newsRepository: NewsRepository) {
    suspend fun execute(article: Article) = newsRepository.deleteArticle(article)
}