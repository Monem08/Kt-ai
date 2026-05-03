package com.monem.ktai.di

import android.content.Context
import androidx.room.Room
import com.monem.ktai.data.local.KtAIDatabase
import com.monem.ktai.data.local.dao.ChatSessionDao
import com.monem.ktai.data.local.dao.FileChangeDao
import com.monem.ktai.data.local.dao.MessageDao
import com.monem.ktai.data.local.dao.UsageDao
import com.monem.ktai.data.local.dao.UserDao
import com.monem.ktai.data.local.dao.WorkspaceDao
import com.monem.ktai.data.remote.ai.AIProvider
import com.monem.ktai.data.remote.ai.PlaceholderAIProvider
import com.monem.ktai.data.repository.AuthRepositoryImpl
import com.monem.ktai.data.repository.ChatRepositoryImpl
import com.monem.ktai.data.repository.FileChangeRepositoryImpl
import com.monem.ktai.data.repository.WorkspaceRepositoryImpl
import com.monem.ktai.domain.repository.AuthRepository
import com.monem.ktai.domain.repository.ChatRepository
import com.monem.ktai.domain.repository.FileChangeRepository
import com.monem.ktai.domain.repository.WorkspaceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KtAIDatabase {
        return Room.databaseBuilder(
            context,
            KtAIDatabase::class.java,
            "ktai_database",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides fun provideUserDao(db: KtAIDatabase): UserDao = db.userDao()
    @Provides fun provideWorkspaceDao(db: KtAIDatabase): WorkspaceDao = db.workspaceDao()
    @Provides fun provideChatSessionDao(db: KtAIDatabase): ChatSessionDao = db.chatSessionDao()
    @Provides fun provideMessageDao(db: KtAIDatabase): MessageDao = db.messageDao()
    @Provides fun provideFileChangeDao(db: KtAIDatabase): FileChangeDao = db.fileChangeDao()
    @Provides fun provideUsageDao(db: KtAIDatabase): UsageDao = db.usageDao()
}

@Module
@InstallIn(SingletonComponent::class)
object AIModule {

    @Provides
    @Singleton
    fun provideAIProvider(): AIProvider = PlaceholderAIProvider()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindWorkspaceRepository(impl: WorkspaceRepositoryImpl): WorkspaceRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindFileChangeRepository(impl: FileChangeRepositoryImpl): FileChangeRepository
}
