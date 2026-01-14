package com.dicoding.storyapp.ui.welcome

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivityWelcomeBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.auth.LoginActivity
import com.dicoding.storyapp.ui.auth.RegisterActivity
import com.dicoding.storyapp.ui.main.MainActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private val viewModel: WelcomeViewModel by viewModels { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin && user.token.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun playAnimation() {
        val views = listOf(binding.ivHero, binding.tvTitle, binding.tvDesc, binding.llButtons)
        views.forEach { it.alpha = 0f }

        ObjectAnimator.ofFloat(binding.ivHero, View.ALPHA, 0f, 1f).setDuration(500).start()
        ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 0f, 1f).setDuration(500).apply { startDelay = 200 }.start()
        ObjectAnimator.ofFloat(binding.tvDesc, View.ALPHA, 0f, 1f).setDuration(500).apply { startDelay = 400 }.start()
        ObjectAnimator.ofFloat(binding.llButtons, View.ALPHA, 0f, 1f).setDuration(500).apply { startDelay = 600 }.start()
    }
}
