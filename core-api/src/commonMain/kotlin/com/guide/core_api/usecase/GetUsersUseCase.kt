package com.guide.core_api.usecase

import com.guide.core_api.Resource
import com.guide.core_api.model.User
import com.guide.core_api.repository.UserRepository

class GetUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Resource<List<User>> {
        return repository.getUsers()
    }
}