package com.rooze.insta_2.dependency_injection.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rooze.insta_2.data.repository.AccountRepositoryImpl
import com.rooze.insta_2.data.repository.ImageRepositoryImpl
import com.rooze.insta_2.data.repository.PostRepositoryImpl
import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.ImageRepository
import com.rooze.insta_2.domain.repository.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    fun provideAccountRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AccountRepository = AccountRepositoryImpl(auth, firestore)

    @Provides
    fun provideImageRepository(
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): ImageRepository = ImageRepositoryImpl(auth, storage)

    @Provides
    fun providePostRepository(
        firestore: FirebaseFirestore
    ): PostRepository = PostRepositoryImpl(firestore)
}