package com.development.clean.util.debug

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.BackgroundColorSpan
import android.util.Pair
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.development.clean.R
import com.development.clean.base.BaseFragment
import com.development.clean.base.BaseViewModel
import com.development.clean.databinding.FragmentLogcatDedailBinding
import com.development.clean.util.Utils
import com.development.clean.util.extension.hideKeyboard
import com.development.clean.util.extension.showKeyboard
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogcatDetailViewModel : BaseViewModel()

class LogcatDetailFragment(private val logInfo: LogInfo) :
    BaseFragment<FragmentLogcatDedailBinding, LogcatDetailViewModel>(
        R.layout.fragment_logcat_dedail
    ) {

    override val viewModel: LogcatDetailViewModel by viewModels()

    private var searchResultList: MutableList<Pair<Int, Int>>? = null
    private var currentIndexScrollTo = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnClose.setOnClickListener {
                requireActivity().onBackPressed()
            }

            btnSendMail.setOnClickListener {
                if (context != null) {
                    Utils.sendEmail(
                        requireContext(),
                        requireContext().applicationInfo
                            .loadLabel(requireContext().packageManager).toString(),
                        logInfo.getContent(), ""
                    )
                }
            }

            edtSearch.showSoftInputOnFocus = true

            btnSearch.setOnClickListener {
                apply {
                    edtSearch.visibility = View.VISIBLE

                    btnSearch.visibility = View.GONE
                    layoutSearchResult.visibility = View.GONE
                    edtSearch.showKeyboard()
                }
            }
            edtSearch.onFocusChangeListener = OnFocusChangeListener { v, hasFocus: Boolean ->
                if (!hasFocus) {
                    hideKeyboard(v)
                    btnSearch.visibility = View.VISIBLE
                    edtSearch.visibility = View.GONE
                }
            }
            edtSearch.setOnEditorActionListener { _, actionId: Int, _: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    edtSearch.clearFocus()
                    viewLifecycleOwner.lifecycleScope.launch {
                        search()
                    }
                    return@setOnEditorActionListener true
                }
                false
            }

            tvContent.setTextIsSelectable(true)
            btnSearchResultUp.setOnClickListener {
                onClickGoToSearchResult(
                    true
                )
            }
            btnSearchResultDown.setOnClickListener {
                onClickGoToSearchResult(
                    false
                )
            }

            viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                tvContent.text = logInfo.getContent()
            }
        }
    }

    private fun onClickGoToSearchResult(goUp: Boolean) {
        if (!searchResultList.isNullOrEmpty()) {
            val lastIndex: Int = currentIndexScrollTo
            if (goUp) {
                if (currentIndexScrollTo > 0) {
                    --currentIndexScrollTo
                } else {
                    currentIndexScrollTo = searchResultList!!.size - 1
                }
                scrollToSearchResult(currentIndexScrollTo)
            } else {
                if (currentIndexScrollTo < searchResultList!!.size - 1) {
                    currentIndexScrollTo++
                } else {
                    currentIndexScrollTo = 0
                }
            }

            //reset last
            val spannable = binding.tvContent.text as SpannableString
            val keyWord: String =
                binding.edtSearch.text.toString().trim { it <= ' ' }.lowercase(Locale.getDefault())
            var indexKeyWord: Int = searchResultList!![lastIndex].second
            var endIndex = indexKeyWord + keyWord.length
            spannable.setSpan(
                BackgroundColorSpan(
                    Color.parseColor("#FF9100")
                ),
                indexKeyWord,
                endIndex,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )

            //highline current
            indexKeyWord = searchResultList!![currentIndexScrollTo].second
            endIndex = indexKeyWord + keyWord.length
            spannable.setSpan(
                BackgroundColorSpan(
                    Color.parseColor("#00E5FF")
                ),
                indexKeyWord,
                endIndex,
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
            scrollToSearchResult(currentIndexScrollTo)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun scrollToSearchResult(index: Int) {
        binding.tvSearchResult.text = (index + 1).toString() + " of " + searchResultList?.size
        binding.scrollWarpperContent.scrollTo(0, searchResultList?.get(index)?.first ?: 0)
    }

    @SuppressLint("SetTextI18n")
    private suspend fun search() {
        searchResultList = null

        with(binding) {
            val searchText: String = edtSearch.text.toString().trim { it <= ' ' }

            if (!TextUtils.isEmpty(searchText)) {
                boldTypingText(
                    tvContent.text.toString(),
                    searchText
                ) { contentResult, searchResultList ->
                    currentIndexScrollTo = 0
                    this@LogcatDetailFragment.searchResultList = searchResultList
                    tvContent.text = contentResult
                    layoutSearchResult.visibility = View.VISIBLE
                    if (searchResultList.size > 0) {
                        val spannable = tvContent.text as SpannableString
                        val keyWord: String =
                            edtSearch.text.toString().trim { it <= ' ' }
                                .lowercase(Locale.getDefault())
                        val indexKeyWord =
                            searchResultList[currentIndexScrollTo].second
                        val endIndex = indexKeyWord + keyWord.length
                        spannable.setSpan(
                            BackgroundColorSpan(
                                Color.parseColor("#00E5FF")
                            ),
                            indexKeyWord,
                            endIndex,
                            Spanned.SPAN_INCLUSIVE_INCLUSIVE
                        )
                        scrollToSearchResult(currentIndexScrollTo)
                    } else {
                        tvSearchResult.text = "0 of 0"
                    }
                }
            } else {
                tvContent.text = logInfo.getContent()
            }
        }
    }

    private suspend fun boldTypingText(
        content: String,
        boldString: String,
        result: (SpannableString, MutableList<Pair<Int, Int>>) -> Unit
    ) {
        withContext(Dispatchers.Default) {
            val searchResultList: MutableList<Pair<Int, Int>> = arrayListOf()
            val spannable = SpannableString(content)
            val textLower = content.lowercase(Locale.getDefault())
            val boldLower = boldString.lowercase(Locale.getDefault())
            val totalIndexBold: MutableList<Int> = arrayListOf()
            var indexBold = textLower.indexOf(boldLower)
            while (indexBold >= 0) {
                val lineNumber: Int = binding.tvContent.layout.getLineForOffset(indexBold)
                val coordinateLineY: Int = binding.tvContent.layout.getLineTop(lineNumber)
                searchResultList.add(Pair(coordinateLineY, indexBold))
                totalIndexBold.add(indexBold)
                indexBold = textLower.indexOf(boldLower, indexBold + boldLower.length)
            }
            for (each in totalIndexBold) {
                val endIndex = each + boldLower.length
                spannable.setSpan(
                    BackgroundColorSpan(
                        Color.parseColor("#FF9100")
                    ),
                    each,
                    endIndex,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
            }

            withContext(Dispatchers.Main) {
                result.invoke(spannable, searchResultList)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(logInfo: LogInfo) =
            LogcatDetailFragment(logInfo)
    }
}
