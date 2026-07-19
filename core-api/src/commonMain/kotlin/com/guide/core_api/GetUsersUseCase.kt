package com.guide.core_api

class GetUsersUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Resource<List<User>> {
        return repository.getUsers()
    }
}