package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.LexiconDatabase
import com.example.data.WordObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [WordObject::class], version = 2, exportSchema = false)
@TypeConverters(LexiTypeConverters::class)
abstract class LexiDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: LexiDatabase? = null

        fun getDatabase(context: Context): LexiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LexiDatabase::class.java,
                    "lexi_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(LexiDatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class LexiDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.wordDao()
                    // Prepopulate with LexiconDatabase words
                    dao.insertWords(LexiconDatabase.words)
                }
            }
        }
    }
}
