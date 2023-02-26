package com.rooze.insta_2.presentation.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rooze.insta_2.R
import com.rooze.insta_2.databinding.FragmentLoginBinding
import com.rooze.insta_2.presentation.MainViewModel
import com.rooze.insta_2.presentation.common.collectWhenActive
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        private const val TAG = "LoginFragment"
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            res.data?.let { handleActivityIntentResult(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView: ${findNavController().backQueue.map { it.destination.label }}")

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = loginViewModel

        binding.avatarImage.setOnClickListener {
            activityLauncher.launch(Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            })
        }

        binding.backButton.setOnClickListener {
            activity?.finish()
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        loginViewModel.message.collectWhenActive(viewLifecycleOwner) { message ->
            context?.applicationContext?.let {
                Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
            }
        }
        loginViewModel.loggedId.collectWhenActive(viewLifecycleOwner) { loggedIn ->
            if (loggedIn) {
                mainViewModel.reloadCurrentAccount()
                findNavController().navigate(R.id.launch_posts_list_from_login)
            }
        }
    }

    private fun handleActivityIntentResult(intent: Intent) {
        Log.i(TAG, "handleActivityIntentResult: ${intent.data}")
        loginViewModel.setImageUri(intent.data.toString())
    }
}