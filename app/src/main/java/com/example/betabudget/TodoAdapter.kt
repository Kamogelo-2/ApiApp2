package com.example.betabudget

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betabudget.data.Todo

class TodoAdapter(private val todos: List<Todo>) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {
    private val TAG = "TodoAdapter"

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.tvTodoDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        Log.d(TAG, "onCreateViewHolder: Creating new view holder")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos[position]
        Log.d(TAG, "onBindViewHolder: Binding '${todo.todo}'")

        if (todo.isSynced) {
            holder.description.text = todo.todo
            // Reset alpha for synced items
            holder.itemView.alpha = 1.0f
        } else {
            // Add a visual indicator for unsynced items
            holder.description.text = "${todo.todo} (Pending sync...)"
            holder.itemView.alpha = 0.6f
        }
    }

    override fun getItemCount() = todos.size
}