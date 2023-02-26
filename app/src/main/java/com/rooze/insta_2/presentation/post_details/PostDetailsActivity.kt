package com.rooze.insta_2.presentation.post_details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.ActivityPostDetailsBinding
import com.rooze.insta_2.presentation.common.ViewConstants
import com.rooze.insta_2.presentation.common.collectWhenActive
import com.rooze.insta_2.presentation.post_details.view_holder.DetailsViewListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailsActivity : AppCompatActivity(), DetailsViewListener, PopupMenu.OnMenuItemClickListener {

    companion object {
        private const val TAG = "PostDetailsActivity"
    }

    private val viewModel: PostDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityPostDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_post_details)

        viewModel.setPostId(intent?.getStringExtra(ViewConstants.EXTRA_POST_ID) ?: "")

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.adapter = PostDetailAdapter(this)

        binding.backButton.setOnClickListener {
            if (viewModel.shouldReload) {
                setResult(
                    RESULT_OK,
                    Intent().apply { putExtra(ViewConstants.EXTRA_RELOAD_POSTS, true) }
                )
            }
            finish()
        }
        binding.menuButton.setOnClickListener {
            val popup = PopupMenu(this, it)
            popup.setOnMenuItemClickListener(this)
            popup.inflate(R.menu.menu_post)
            popup.show()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.message.collectWhenActive(this) { message ->
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
        viewModel.deleted.collectWhenActive(this) { isDeleted ->
            if (isDeleted) {
                setResult(
                    RESULT_OK,
                    Intent().apply { putExtra(ViewConstants.EXTRA_RELOAD_POSTS, true) }
                )
                finish()
            }
        }
    }

    override fun like() {
        viewModel.like()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
                Log.i(TAG, "onMenuItemClick: delete")
                viewModel.delete()
                return true
            }
            R.id.edit -> {
                Log.i(TAG, "onMenuItemClick: edit")
                return true
            }
        }
        return false
    }
}