package com.rooze.insta_2.presentation.upload

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.use_case.UploadPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(private val uploadPost: UploadPost) : ViewModel() {

    companion object {
        private const val TAG = "UploadViewModel"
    }

    private val _imageUri = MutableStateFlow("")
    private val _loading = MutableStateFlow(false)
    private val _message = MutableSharedFlow<String>()
    private val _posted = MutableStateFlow(false)

    val content = ObservableField("")
    val imageUri get(): StateFlow<String> = _imageUri
    val loading get(): StateFlow<Boolean> = _loading
    val message get(): SharedFlow<String> = _message
    val posted get(): StateFlow<Boolean> = _posted

    fun upload(context: Context) {
        _loading.value = true
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG, "upload: ", throwable)
            viewModelScope.launch {
                if (throwable is FileNotFoundException) {
                    _message.emit("Image data error")
                } else {
                    _message.emit("Posting error!")
                }
            }
        }) {
            val uploadResult = uploadPost(
                content.get() ?: "",
                context.contentResolver?.openInputStream(Uri.parse(imageUri.value))
            )
            when (uploadResult) {
                is DataResult.Success -> {
                    _message.emit("Post success")
                    _posted.value = true
                }
                is DataResult.Fail -> _message.emit(uploadResult.message)
            }
        }.invokeOnCompletion { _loading.value = false }
    }

    fun setImageUri(uri: String) {
        _imageUri.value = uri
    }
}