package com.monem.ktai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.monem.ktai.data.local.dao.ChatSessionDao
import com.monem.ktai.data.local.dao.FileChangeDao
import com.monem.ktai.data.local.dao.MessageDao
import com.monem.ktai.data.local.dao.UsageDao
import com.monem.ktai.data.local.dao.UserDao
import com.monem.ktai.data.local.dao.WorkspaceDao
import com.monem.ktai.data.local.entity.ChatSessionEntity
import com.monem.ktai.data.local.entity.FileChangeEntity
import com.monem.ktai.data.local.entity.MessageEntity
import com.monem.ktai.data.local.entity.UsageRecordEntity
import com.monem.ktai.data.local.entity.UserProfileEntity
import com.monem.ktai.data.local.entity.WorkspaceEntity

@Database(
    entities = [
        UserProfileEntity::class,
        WorkspaceEntity::class,
        ChatSessionEntity::class,
        MessageEntity::class,
        FileChangeEntity::class,
        UsageRecordEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class KtAIDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun chatSessionDao(): ChatSessionDao
    abstract fun messageDao(): MessageDao
    abstract fun fileChangeDao(): FileChangeDao
    abstract fun usageDao(): UsageDao
}
