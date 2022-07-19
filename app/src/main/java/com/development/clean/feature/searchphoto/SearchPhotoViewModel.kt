package com.development.clean.feature.searchphoto

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.development.clean.base.BaseViewModel
import com.development.clean.data.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchPhotoViewModel @Inject constructor(
    mainRepository: MainRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {

    val query = MutableStateFlow<String?>(savedStateHandle[QUERY_SAVED_STATE_KEY])

    private var _currentGetPhotoJob: Job? = null
    val photos = query
        .flatMapLatest {
            _currentGetPhotoJob?.cancel()
            viewModelScope.async {
                mainRepository.getPhotos(it ?: "tree")
                    .cancellable()
            }.also {
                _currentGetPhotoJob = it
            }.await()
        }.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            query.collect {
                savedStateHandle[QUERY_SAVED_STATE_KEY] = it
            }
        }
    }

/*    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(query: String?): SearchPhotoViewModel
    }*/

    companion object {
        const val QUERY_SAVED_STATE_KEY = "query"

/*        fun provideFactory(
            assistedFactory: AssistedFactory,
            query: String?,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(query) as T
            }
        }*/
    }
}