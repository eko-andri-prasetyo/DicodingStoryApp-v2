package com.dicoding.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityRegisterBinding
import com.dicoding.storyapp.ui.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text?.toString().orEmpty().trim()
            val email = binding.edRegisterEmail.text?.toString().orEmpty().trim()
            val password = binding.edRegisterPassword.text?.toString().orEmpty()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Nama, email, dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.edRegisterPassword.error != null || binding.edRegisterEmail.error != null) {
                Toast.makeText(this, "Periksa kembali input yang masih error", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(name, email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        val body = result.data
                        Toast.makeText(this, body.message ?: "Register berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
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
        binding.btnRegister.isEnabled = !isLoading
    }
}
