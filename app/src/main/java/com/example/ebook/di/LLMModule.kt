package com.example.ebook.di

import com.example.ebook.data.local.ai.LocalLlmEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LLMModule {

    @Provides
    @Singleton
    fun provideLocalLlmEngine(): LocalLlmEngine {
        return LocalLlmEngine()
    }
}
