package com.example.androidlearning

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.androidlearning.databinding.ActivityMainBinding
import com.example.androidlearning.presentation.adapter.NewsAdapter
import com.example.androidlearning.presentation.viewmodel.NewsViewModel
import com.example.androidlearning.presentation.viewmodel.NewsViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var factory: NewsViewModelFactory
    @Inject
    lateinit var newsAdapter: NewsAdapter
    lateinit var viewModel: NewsViewModel

    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this,factory)
            .get(NewsViewModel::class.java)

        val navHostFragment=supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        val navController=navHostFragment.navController
        binding.bottomNavigationNews.setupWithNavController(
            navController
        )
    }
}