package com.example.betabudget.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.betabudget.LoginActivity
import com.example.betabudget.R
import com.example.betabudget.data.TodoRepository
import com.example.betabudget.util.SessionManager
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.util.Locale



class SettingsFragment : Fragment() {

    private val TAG = "SettingsFragment"

    // --- UI ---
    private lateinit var ivProfilePic: ImageView
    private lateinit var btnChangePicture: Button
    private lateinit var switchNotifications: SwitchMaterial
    private lateinit var tvSelectLanguage: TextView
    private lateinit var tvCurrentLanguage: TextView
    private lateinit var tvLogout: TextView

    // --- Data ---
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: TodoRepository

    // --- Firebase ---
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var userId: String? = null

    // --- Activity Launcher for Photo Picker ---
    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d(TAG, "Photo selected: $uri")
            uploadImageToFirebase(uri)
        } else {
            Log.w(TAG, "No photo selected.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init Firebase
        db = Firebase.firestore
        storage = Firebase.storage
        userId = Firebase.auth.currentUser?.uid

        // Init Data
        sessionManager = SessionManager(requireContext())
        repository = TodoRepository(requireContext())

        // Find views
        ivProfilePic = view.findViewById(R.id.ivProfilePic)
        btnChangePicture = view.findViewById(R.id.btnChangePicture)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        tvSelectLanguage = view.findViewById(R.id.tvSelectLanguage)
        tvCurrentLanguage = view.findViewById(R.id.tvCurrentLanguage)
        tvLogout = view.findViewById(R.id.tvLogout)

        loadSettings()
        loadProfilePicture() // Load existing picture

        // --- Set Listeners ---
        btnChangePicture.setOnClickListener {
            Log.d(TAG, "Change Picture clicked.")
            // Launch the photo picker
            photoPickerLauncher.launch("image/*")
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sessionManager.saveNotificationSetting(isChecked)
        }

        tvSelectLanguage.setOnClickListener {
            showLanguageDialog()
        }

        tvLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadProfilePicture() {
        if (userId == null) return

        db.collection("users").document(userId!!).get()
            .addOnSuccessListener { document ->
                if (document != null && document.contains("profileImageUrl")) {
                    val imageUrl = document.getString("profileImageUrl")
                    Log.d(TAG, "Loading profile image: $imageUrl")
                    // Use Glide to load the image from the URL
                    Glide.with(this)
                        .load(imageUrl)
                        .circleCrop() // Make it circular
                        .into(ivProfilePic)
                } else {
                    Log.d(TAG, "No profile image URL found in Firestore.")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error loading profile image URL", e)
            }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        if (userId == null) return

        Toast.makeText(context, "Uploading picture...", Toast.LENGTH_SHORT).show()

        // This is the "path" in your blob storage
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                Log.d(TAG, "Image uploaded successfully.")
                // Get the download URL
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d(TAG, "Download URL: $downloadUri")
                    // Save this URL to our NoSQL database (Firestore)
                    saveProfilePicUrl(downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Image upload failed", e)
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfilePicUrl(url: String) {
        if (userId == null) return

        // Save the URL to the user's document
        val data = mapOf("profileImageUrl" to url)
        db.collection("users").document(userId!!)
            .set(data, SetOptions.merge()) // SetOptions.merge() updates without overwriting
            .addOnSuccessListener {
                Log.i(TAG, "Profile image URL saved to Firestore.")
                Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                // Reload the image in the UI
                Glide.with(this).load(url).circleCrop().into(ivProfilePic)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error saving URL to Firestore", e)
            }
    }

    private fun logout() {
        Log.i(TAG, "Logout button clicked.")
        sessionManager.clearData()
        viewLifecycleOwner.lifecycleScope.launch {
            repository.clearLocalData()
        }
        goToLogin()
    }

    // ... (loadSettings, updateLanguageText, showLanguageDialog, setAppLocale, goToLogin)
    // ... (These functions remain exactly the same as before)
}