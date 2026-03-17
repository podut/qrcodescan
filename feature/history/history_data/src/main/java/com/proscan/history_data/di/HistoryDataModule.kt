package com.proscan.history_data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.proscan.history_data.local.ProScanDatabase
import com.proscan.history_data.local.ScanHistoryDao
import com.proscan.history_data.repository.HistoryRepositoryImpl
import com.proscan.history_domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE scan_history ADD COLUMN source TEXT NOT NULL DEFAULT 'SCANNED'")
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryDataModule {

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        impl: HistoryRepositoryImpl
    ): HistoryRepository

    companion object {
        @Provides
        @Singleton
        fun provideProScanDatabase(
            @ApplicationContext context: Context
        ): ProScanDatabase {
            return Room.databaseBuilder(
                context,
                ProScanDatabase::class.java,
                ProScanDatabase.DATABASE_NAME
            )
            .addMigrations(MIGRATION_1_2)
            .build()
        }

        @Provides
        @Singleton
        fun provideScanHistoryDao(database: ProScanDatabase): ScanHistoryDao {
            return database.scanHistoryDao
        }
    }
}
