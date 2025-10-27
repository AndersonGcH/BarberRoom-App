package com.example.barberroom.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.barberroom.databinding.ActivityLoginBinding
import com.example.barberroom.ui.activity.MainActivity
import com.example.barberroom.ui.activity.BarberoActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            iniciarSesionConFirebase(email, password)
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun iniciarSesionConFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    checkUserRoleAndRedirect()
                } else {
                    Toast.makeText(
                        this,
                        "Error en el inicio de sesión",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
    private fun checkUserRoleAndRedirect() {
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        db.collection("barberos").document(uid).get()
            .addOnSuccessListener { document ->
                val intent = if (document.exists()) {
                    Intent(this, BarberoActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}