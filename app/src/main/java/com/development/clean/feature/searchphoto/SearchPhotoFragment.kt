package com.development.clean.feature.searchphoto

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.development.clean.R
import com.development.clean.base.BaseFragment
import com.development.clean.common.adapter.PagingLoadStateAdapter
import com.development.clean.databinding.FragmentSearchPhotoBinding
import com.development.clean.util.NetworkMonitor
import com.development.clean.util.extension.launchAndCollectLatestIn
import com.development.clean.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchPhotoFragment :
    BaseFragment<FragmentSearchPhotoBinding, SearchPhotoViewModel>(R.layout.fragment_search_photo) {

/*    @Inject
    internal lateinit var viewModelFactory: SearchPhotoViewModel.AssistedFactory

    override val viewModel: SearchPhotoViewModel by viewModels {
        SearchPhotoViewModel.provideFactory(viewModelFactory, args.query)
    }*/

    override val viewModel: SearchPhotoViewModel by viewModels()

    private val args: SearchPhotoFragmentArgs by navArgs()

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoAdapter = PhotoAdapter()

        binding.apply {
            vm = viewModel
            recyclerView.adapter = photoAdapter.withLoadStateFooter(PagingLoadStateAdapter())
        }

        photoAdapter.addLoadStateListener {
            val stateError = it.refresh as? LoadState.Error
                ?: it.prepend as? LoadState.Error
                ?: it.append as? LoadState.Error

            stateError?.error?.let { e ->
                showToast(e.toString())
            }
        }

        setUpMenu()

        viewModel.photos.launchAndCollectLatestIn(viewLifecycleOwner) {
            photoAdapter.submitData(it)
        }
        networkMonitor.connectStateFlow.launchAndCollectLatestIn(viewLifecycleOwner) {
            showToast("Internet connection is ${if (it) "available" else "unavailable"}")
        }
    }

    private fun setUpMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search_photo, menu)

                val searchManager =
                    requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager

                (menu.findItem(R.id.action_search).actionView as SearchView).apply {
                    setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
                    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            viewModel.query.value = query
                            return false
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            return true
                        }
                    })
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_new -> {
                        findNavController().navigate(SearchPhotoFragmentDirections.actionToAnother("Rose"))
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}