
package com.habitflow.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitflow.app.data.local.dao.HabitCompletionDao
import com.habitflow.app.data.local.dao.HabitDao
import com.habitflow.app.data.local.entity.HabitCompletionEntity
import com.habitflow.app.data.local.entity.HabitEntity

@Database(
    entities = [HabitEntity::class, HabitCompletionEntity::class],
    version = 3,
    exportSchema = true
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao

    companion object {

        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habitflow_database"
                )

                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
