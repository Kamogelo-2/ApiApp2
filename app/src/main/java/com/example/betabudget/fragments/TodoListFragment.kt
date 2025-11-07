package com.example.betabudget.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betabudget.AddTodoActivity
import com.example.betabudget.R
import com.example.betabudget.TodoAdapter
import com.example.betabudget.data.Todo
import com.example.betabudget.data.TodoRepository
import com.example.betabudget.util.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class TodoListFragment : Fragment() {

    private val TAG = "TodoListFragment"

    // UI
    private lateinit var rvTodos: RecyclerView
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var fabAddTodo: FloatingActionButton
    private lateinit var tvEmptyView: TextView

    // Data
    private var todoList = mutableListOf<Todo>()
    private lateinit var repository: TodoRepository

    // Session
    private lateinit var sessionManager: SessionManager
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Use requireContext() inside fragments
        sessionManager = SessionManager(requireContext())
        repository = TodoRepository(requireContext())
        userId = sessionManager.fetchUserId()

        // Find views using the fragment's 'view'
        rvTodos = view.findViewById(R.id.rvTodos)
        fabAddTodo = view.findViewById(R.id.fabAddTodo)
        tvEmptyView = view.findViewById(R.id.tvEmptyView)

        setupRecyclerView()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Run sync and reload list every time we return
        if (userId != -1) {
            syncAndLoadTodos()
        }
    }

    private fun setupClickListeners() {
        fabAddTodo.setOnClickListener {
            Log.i(TAG, "FAB (Add Todo) clicked.")
            startActivity(Intent(requireContext(), AddTodoActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: Initializing.")
        todoAdapter = TodoAdapter(todoList)
        rvTodos.adapter = todoAdapter
        rvTodos.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun syncAndLoadTodos() {
        Log.i(TAG, "syncAndLoadTodos: Starting sync...")

        // Use viewLifecycleOwner.lifecycleScope in fragments
        viewLifecycleOwner.lifecycleScope.launch {
            repository.syncOfflineTodos(userId)
            Log.i(TAG, "Sync finished. Loading all todos for display...")

            val todos = repository.getAllTodos(userId)

            Log.d(TAG, "loadTodos: Success. Found ${todos.size} total todos.")
            todoList.clear()
            todoList.addAll(todos)
            todoAdapter.notifyDataSetChanged()
            checkEmptyView()
        }
    }

    private fun checkEmptyView() {
        if (todoList.isEmpty()) {
            rvTodos.visibility = View.GONE
            tvEmptyView.visibility = View.VISIBLE
        } else {
            rvTodos.visibility = View.VISIBLE
            tvEmptyView.visibility = View.GONE
        }
    }
}