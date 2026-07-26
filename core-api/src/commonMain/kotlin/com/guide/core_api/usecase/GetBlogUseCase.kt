package com.guide.core_api.usecase

import com.guide.core_api.Resource
import com.guide.core_api.model.BloggerResponse
import com.guide.core_api.repository.BlogRepository

class GetBlogUseCase(
    private val repository: BlogRepository
) {
    suspend operator fun invoke(url : String): Resource<BloggerResponse> {
        return repository.getBlogResponse(url)
    }
}