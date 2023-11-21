package com.streamwide.fileencrypter.di

import android.content.Context
import com.streamwide.domain.repository.IFileRepository
import com.streamwide.fileencrypter.data.EncryptionFileDB
import com.streamwide.fileencrypter.data.repository.FileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//IMPORT

@InstallIn(SingletonComponent::class)
@Module
object AppModule {


    @Singleton
    @Provides
    fun database(
        @ApplicationContext context: Context
    ): EncryptionFileDB = EncryptionFileDB.getDatabase(context = context)

    @Singleton
    @Provides
    fun repository(
        database : EncryptionFileDB
    ): IFileRepository = FileRepository(database.fileDao())


}
