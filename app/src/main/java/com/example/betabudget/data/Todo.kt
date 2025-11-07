package com.example.betabudget.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(// This localId is for Room and must be the Primary Key
    @PrimaryKey(autoGenerate = true)
    var localId: Long = 0,

    // This is the ID from the dummyjson API. It's nullable
    // because locally-created items won't have one yet.
    val id: Int? = null,
    val todo: String,
    val completed: Boolean,
    val userId: Int,
    // This is the flag for our sync logic
    var isSynced: Boolean = false
)

