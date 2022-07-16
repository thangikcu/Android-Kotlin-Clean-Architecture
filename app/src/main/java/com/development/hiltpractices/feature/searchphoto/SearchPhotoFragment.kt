package com.development.hiltpractices.feature.searchphoto

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
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.development.hiltpractices.R
import com.development.hiltpractices.base.BaseFragment
import com.development.hiltpractices.common.adapter.PagingLoadStateAdapter
import com.development.hiltpractices.databinding.FragmentSearchPhotoBinding
import com.development.hiltpractices.util.extension.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoAdapter = PhotoAdapter()

        binding.apply {
            vm = viewModel
            recyclerView.adapter = photoAdapter.withLoadStateFooter(PagingLoadStateAdapter())
        }

        photoAdapter.addLoadStateListener {
            (it.refresh as? LoadState.Error)?.error?.let { e ->
                showToast(e.toString())
            }
        }

        setUpMenu()

        with(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewModel.photos.flowWithLifecycle(lifecycle).collectLatest {
                    photoAdapter.submitData(it)
                }
            }
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