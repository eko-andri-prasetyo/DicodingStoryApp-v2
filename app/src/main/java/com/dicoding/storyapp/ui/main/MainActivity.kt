package com.dicoding.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.add.AddStoryActivity
import com.dicoding.storyapp.ui.detail.DetailActivity
import com.dicoding.storyapp.ui.maps.MapsActivity
import com.dicoding.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { ViewModelFactory.getInstance(this) }
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupAction()
        observeSession()
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter { story ->
            startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra(DetailActivity.EXTRA_STORY, story)
            })
        }
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        adapter.addLoadStateListener { loadState ->
            binding.progressBar.isVisible = loadState.source.refresh is androidx.paging.LoadState.Loading
            binding.tvEmpty.isVisible = loadState.source.refresh is androidx.paging.LoadState.NotLoading && adapter.itemCount == 0
        }
    }

    private fun setupAction() {
        binding.swipeRefresh.setOnRefreshListener {
            adapter.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun observeSession() {
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin || user.token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            } else {
                viewModel.story.observe(this) {
                    adapter.submitData(lifecycle, it)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.action_logout -> {
                viewModel.logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
