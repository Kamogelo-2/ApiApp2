package com.example.betabudget.net
import com.example.betabudget.data.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {@POST("auth/login")
suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // Note: dummyjson.com simulates this. It doesn't really create a new user.
    @POST("users/add")
    suspend fun register(@Body userData: Map<String, String>): Response<LoginResponse>

    @GET("users/{id}/todos")
    suspend fun getUserTodos(@Path("id") userId: Int): Response<TodoListResponse>

    // Note: This will return a new Todo with a new ID, but it won't be
    // permanently saved on the server. It's just for testing.
    @POST("todos/add")
    suspend fun addTodo(@Body todo: Todo): Response<Todo>
}