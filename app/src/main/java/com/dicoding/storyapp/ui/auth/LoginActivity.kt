package com.dicoding.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text?.toString().orEmpty().trim()
            val password = binding.edLoginPassword.text?.toString().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.edLoginPassword.error != null || binding.edLoginEmail.error != null) {
                Toast.makeText(this, "Periksa kembali input yang masih error", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        val body = result.data
                        if (body.error == true) {
                            Toast.makeText(this, body.message ?: "Login gagal", Toast.LENGTH_SHORT).show()
                        } else {
                            val token = body.loginResult?.token.orEmpty()
                            val name = body.loginResult?.name.orEmpty()
                            viewModel.saveSession(UserModel(name = name, token = token, isLogin = true))

                            startActivity(Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                            finish()
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }
}
