package com.rooze.insta_2.presentation.upload

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.ActivityUploadBinding
import com.rooze.insta_2.presentation.common.ViewConstants
import com.rooze.insta_2.presentation.common.collectWhenActive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadActivity : AppCompatActivity() {

    private val viewModel: UploadViewModel by viewModels()

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            res.data?.let { handleActivityIntentResult(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityUploadBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_upload
        )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.imageContainer.setOnClickListener {
            activityLauncher.launch(Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            })
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.message.collectWhenActive(this) { message ->
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
        viewModel.posted.collectWhenActive(this) { posted ->
            if (posted) {
                setResult(
                    RESULT_OK,
                    Intent().apply { putExtra(ViewConstants.EXTRA_RELOAD_POSTS, true) }
                )
                finish()
            }
        }
    }

    private fun handleActivityIntentResult(intent: Intent) {
        viewModel.setImageUri(intent.data.toString())
    }
}