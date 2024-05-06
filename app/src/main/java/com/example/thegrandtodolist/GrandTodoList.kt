package com.example.thegrandtodolist

import androidx.annotation.StringRes

data class GrandTodoList(
    val todoId: Int,
    val todoText: String,
    var isCompleted: Boolean = false //Set isCompleted value to true when the task is clicked
)
