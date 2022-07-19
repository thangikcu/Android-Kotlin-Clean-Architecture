package com.development.clean.util.debug

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.development.clean.R
import com.development.clean.base.BaseActivity
import com.development.clean.base.BaseRecyclerAdapter
import com.development.clean.base.BaseViewModel
import com.development.clean.databinding.ActivityLogcatBinding
import com.development.clean.databinding.ItemLogcatBinding
import com.development.clean.util.extension.inTransaction

class LogcatViewModel : BaseViewModel()

@Suppress("DEPRECATION")
class LogcatActivity :
    BaseActivity<ActivityLogcatBinding, LogcatViewModel>(R.layout.activity_logcat) {

    override val viewModel: LogcatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = getColor(R.color.black)

        binding.btnClose.setOnClickListener { finish() }

        val logListAdapter = LogListAdapter { logInfo, _, _ ->
            supportFragmentManager.inTransaction {
                add(R.id.content, LogcatDetailFragment.newInstance(logInfo))
                    .addToBackStack("logcatDetail")
            }
        }.apply {
            submitList(Logcat.logs)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@LogcatActivity)
            adapter = logListAdapter
        }

        Logcat.onLogsUpdate = {
            binding.recyclerView.post {
                logListAdapter.notifyDataSetChanged()
            }
        }

        binding.tvMenu.setOnClickListener {
            PopupMenu(this, it).apply {
                menu.add("Clear logs")
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.title.toString()) {
                        "Clear logs" -> {
                            Logcat.clearLog()
                            logListAdapter.notifyDataSetChanged()
                        }
                        else -> {
                        }
                    }
                    true
                }
                show()
            }
        }
    }

    override fun onDestroy() {
        Logcat.onLogsUpdate = null
        super.onDestroy()
    }
}

class LogListAdapter(override val onItemClick: ((LogInfo, View, Int) -> Unit)) :
    BaseRecyclerAdapter<LogInfo, ItemLogcatBinding>() {

    override fun getLayoutRes(viewType: Int) = R.layout.item_logcat

    override fun bindView(binding: ItemLogcatBinding, item: LogInfo, position: Int) {
        super.bindView(binding, item, position)
        binding.tvTitle.setTextColor(
            if (item.loadFromCache) {
                Color.YELLOW
            } else Color.BLACK
        )
    }
}
