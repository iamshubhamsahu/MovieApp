package com.example.movieapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.adapter.LoadMoreAdapter
import com.example.movieapp.adapter.MoviesAdapter
import com.example.movieapp.databinding.FragmentMoviesBinding
import com.example.movieapp.viewmodel.MoviesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MoviesFragment : Fragment() {

    private lateinit var binding: FragmentMoviesBinding

    @Inject
    lateinit var moviesAdapter: MoviesAdapter



    private val viewModel: MoviesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentMoviesBinding.inflate(layoutInflater)

        // binding = FragmentMoviesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            lifecycleScope.launchWhenCreated {
                viewModel.moviesList.collect {
                    moviesAdapter.submitData(it)
                }
            }

            moviesAdapter.setOnItemClickListener {
                val direction = MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(it.id)
                findNavController().navigate(direction)
            }

            lifecycleScope.launchWhenCreated {
                moviesAdapter.loadStateFlow.collect{
                    val state = it.refresh
                    prgBarMovies.isVisible = state is LoadState.Loading
                }
            }


            rlPopular.apply {
                layoutManager = LinearLayoutManager(context , LinearLayoutManager.HORIZONTAL,false)
                // layoutManager = LinearLayoutManager(requireContext())
                adapter = moviesAdapter
            }

            rlPopular.adapter=moviesAdapter.withLoadStateFooter(
                LoadMoreAdapter{
                    moviesAdapter.retry()
                }
            )

        }
    }

}