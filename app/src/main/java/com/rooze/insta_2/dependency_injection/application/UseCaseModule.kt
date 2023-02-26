package com.rooze.insta_2.dependency_injection.application

import com.rooze.insta_2.domain.repository.AccountRepository
import com.rooze.insta_2.domain.repository.ImageRepository
import com.rooze.insta_2.domain.repository.PostRepository
import com.rooze.insta_2.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {
    @Provides
    fun provideAuthentication(
        accountRepository: AccountRepository,
        imageRepository: ImageRepository
    ): Authentication = Authentication(accountRepository, imageRepository)

    @Provides
    fun providePostsList(
        postRepository: PostRepository,
        accountRepository: AccountRepository
    ): PostsList = PostsList(postRepository, accountRepository)

    @Provides
    fun provideProfileInfo(
        postRepository: PostRepository,
        accountRepository: AccountRepository
    ): ProfileInfo = ProfileInfo(postRepository, accountRepository)

    @Provides
    fun provideUploadPost(
        postRepository: PostRepository,
        imageRepository: ImageRepository,
        accountRepository: AccountRepository
    ): UploadPost = UploadPost(postRepository, imageRepository, accountRepository)

    @Provides
    fun providePostDetails(
        accountRepository: AccountRepository,
        postRepository: PostRepository,
        imageRepository: ImageRepository
    ): PostDetails = PostDetails(postRepository, accountRepository, imageRepository)

    @Provides
    fun provideEditAccount(
        accountRepository: AccountRepository,
        imageRepository: ImageRepository
    ): EditAccount = EditAccount(accountRepository, imageRepository)
}