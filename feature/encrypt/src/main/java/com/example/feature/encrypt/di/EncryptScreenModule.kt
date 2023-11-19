package com.example.feature.encrypt.di

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class EncryptScreenModule() {

    @Provides
    fun provideContentResolver(@ApplicationContext app: Context): ContentResolver {
        return app.contentResolver
    }
}