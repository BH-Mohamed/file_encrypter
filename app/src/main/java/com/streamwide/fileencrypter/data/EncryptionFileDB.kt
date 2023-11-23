package com.streamwide.fileencrypter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.streamwide.fileencrypter.data.dao.FileDao
import com.streamwide.fileencrypter.data.entity.FileEntity

@Database(entities = [FileEntity::class], version = 3, exportSchema = false)
abstract class EncryptionFileDB : RoomDatabase() {

    abstract fun fileDao(): FileDao

    companion object {

        @Volatile
        private var INSTANCE: EncryptionFileDB? = null

        fun getDatabase(context: Context): EncryptionFileDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EncryptionFileDB::class.java,
                    "encrypted_files_database"
                )
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }

    }

}
