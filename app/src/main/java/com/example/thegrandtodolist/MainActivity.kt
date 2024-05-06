package com.example.thegrandtodolist

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.thegrandtodolist.ui.theme.TheGrandTodoListTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : ComponentActivity() {

    private val todoList = mutableListOf<GrandTodoList>()
    private lateinit var adapter: ArrayAdapter<GrandTodoList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val listView: ListView = findViewById(R.id.todoListText)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todoList)
        listView.adapter = adapter

        val editTextTodo: EditText = findViewById(R.id.inputTask)
        val buttonAddTodo: FloatingActionButton = findViewById(R.id.addTaskButton)

        buttonAddTodo.setOnClickListener{
            val todoText = editTextTodo.text.toString().trim()
            if(todoText.isNotEmpty()) {
                val newTodo = GrandTodoList(todoList.size + 1, todoText)
                todoList.add(newTodo)
                adapter.notifyDataSetChanged()
                editTextTodo.text.clear()
            } else{
                Toast.makeText(this, "Please enter a task you would like to complete.",
                    Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemLongClickListener { parent, view, position, id ->
            val removedTodo = todoList.removeAt(position)
            adapter.notifyDataSetChanged()
            Toast.makeText(
                this, "Deleted: ${removedTodo.todoText}",
                Toast.LENGTH_SHORT
            ).show()
            true // Return true to consume the long click event
        }
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
