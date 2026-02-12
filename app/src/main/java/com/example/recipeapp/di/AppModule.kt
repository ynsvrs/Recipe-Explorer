package com.example.recipeapp.di

import com.example.recipeapp.data.remote.RecipeApi
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import androidx.room.Room
import com.example.recipeapp.data.local.AppDatabase
import com.example.recipeapp.data.local.RecipeDao
import com.example.recipeapp.domain.repository.RecipeRepository
import com.example.recipeapp.data.repository.RecipeRepositoryImpl

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
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeApi(retrofit: Retrofit): RecipeApi {
        return retrofit.create(RecipeApi::class.java)
    }
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "recipe_db"
        ).build()
    }

    @Provides
    fun provideRecipeDao(db: AppDatabase): RecipeDao =
        db.recipeDao()

    @Provides
    @Singleton
    fun provideRecipeRepository(
        api: RecipeApi,
        dao: RecipeDao
    ): RecipeRepository {
        return RecipeRepositoryImpl(api, dao)
    }



}
