package com.example.thegrandtodolist

import android.content.ContentValues
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.thegrandtodolist.ui.theme.TheGrandTodoListTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : ComponentActivity() {

    private lateinit var todoList: MutableList<GrandTodoList>
    private lateinit var adapter: ArrayAdapter<GrandTodoList>
    private lateinit var dbHelper: TodoDatabaseHelper
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        dbHelper = TodoDatabaseHelper(this)
        todoList = mutableListOf()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todoList)

        listView = findViewById(R.id.todoListText)
        listView.adapter = adapter

        loadTodoItems()

        val editTextTodo: EditText = findViewById(R.id.inputTask)
        val buttonAddTodo: FloatingActionButton = findViewById(R.id.addTaskButton)

        // Add new items to the list when the add button is clicked.
        buttonAddTodo.setOnClickListener{
            val todoText = editTextTodo.text.toString().trim()
            if(todoText.isNotEmpty()) {
                val newTodo = GrandTodoList(todoList.size + 1, todoText)
                todoList.add(newTodo)
                adapter.notifyDataSetChanged()
                saveTodoItem(newTodo)
                editTextTodo.text.clear()
            } else{
                Toast.makeText(this, "Please enter a task you would like to complete.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val clickedTodo = todoList[position]
            // Toggle the completion status of the clicked todo item
            clickedTodo.isCompleted = !clickedTodo.isCompleted
            adapter.notifyDataSetChanged()
            updateTodoItem(clickedTodo)
        }

        listView.setOnItemLongClickListener { parent, view, position, id ->
            val removedTodo = todoList.removeAt(position)
            adapter.notifyDataSetChanged()
            Toast.makeText(
                this, "Deleted: ${removedTodo.todoText}",
                Toast.LENGTH_SHORT
            ).show()

            deleteTodoItem(removedTodo)
            true // Return true to consume the long click event
        }
    }

    private fun loadTodoItems() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TodoDatabaseHelper.TABLE_NAME, null, null, null,null,null,null)

        if(cursor != null && cursor.moveToFirst()) {
            do {
                val todoText =
                    cursor.getString(cursor.getColumnIndex(TodoDatabaseHelper.COLUMN_TASK))
                val todo = GrandTodoList(0, todoText)
                todoList.add(todo)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        adapter.notifyDataSetChanged()
    }

    private fun saveTodoItem(todo: GrandTodoList) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TodoDatabaseHelper.COLUMN_TASK, todo.todoText)
        }
        db.insert(TodoDatabaseHelper.TABLE_NAME, null, values)
    }

    private fun updateTodoItem(todo: GrandTodoList) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TodoDatabaseHelper.COLUMN_TASK, todo.todoText)
        }
        db.update(TodoDatabaseHelper.TABLE_NAME, values, "${TodoDatabaseHelper.COLUMN_TASK} " +
                "= ?", arrayOf(todo.todoText))
    }

    private fun deleteTodoItem(todo: GrandTodoList) {
        val db = dbHelper.writableDatabase
        db.delete(TodoDatabaseHelper.TABLE_NAME, "${TodoDatabaseHelper.COLUMN_TASK} = ?",
            arrayOf(todo.todoText))
    }
}

@Composable
fun GrandTodoListApp(todoList: List<GrandTodoList>) {
    // Pass the todoList parameter here
    TodoList(todoList = todoList)
}

@Composable
fun TodoList(todoList: List<GrandTodoList>) {
    Column {
        for(todo in todoList){
            Text(text = todo.todoText)
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewMainActivity() {
    val todoList = listOf(
        GrandTodoList(todoId = 1, todoText = "Task 1"),
        GrandTodoList(todoId = 2, todoText = "Task 2")
    )
    TheGrandTodoListTheme {
        GrandTodoListApp(todoList = todoList)
    }
}
