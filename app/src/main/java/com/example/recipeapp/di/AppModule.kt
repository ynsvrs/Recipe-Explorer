package com.example.recipeapp.di

import com.example.recipeapp.data.repository.AuthRepositoryImpl
import com.example.recipeapp.data.repository.CommentRepositoryImpl
import com.example.recipeapp.data.repository.MealPlanRepositoryImpl
import com.example.recipeapp.domain.repository.AuthRepository
import com.example.recipeapp.domain.repository.CommentRepository
import com.example.recipeapp.domain.repository.MealPlanRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideMealPlanRepository(
        database: FirebaseDatabase,
        auth: FirebaseAuth
    ): MealPlanRepository = MealPlanRepositoryImpl(database, auth)

    @Provides
    @Singleton
    fun provideCommentRepository(
        database: FirebaseDatabase,
        auth: FirebaseAuth
    ): CommentRepository = CommentRepositoryImpl(database, auth)
}