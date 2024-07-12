package com.aitgacem.openmal.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.aitgacem.openmal.R
import com.aitgacem.openmal.databinding.FragmentLoginPromptBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginPromptFragment : Fragment() {
    private lateinit var binding: FragmentLoginPromptBinding
    private val loginViewModel: LoginViewModel by hiltNavGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginPromptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            loginViewModel.launchBrowserForLogin { intent->
                startActivity(intent)
            }
        }
        val authorizationCode = LoginPromptFragmentArgs.fromBundle(arguments ?: Bundle()).authorizationCode
        authorizationCode?.let { code ->
            loginViewModel.initiateLogin(code)
            binding.button.visibility = GONE
            binding.loading.visibility = VISIBLE
            binding.textView.text = getString(R.string.attempting_login)
            loginViewModel.loginError.observe(viewLifecycleOwner){errorOccured ->
                if(errorOccured){
                    binding.loading.visibility = GONE
                    binding.textView.text = getString(R.string.login_error_occured)
                    binding.button.text = getString(R.string.go_back)
                    binding.button.visibility = VISIBLE
                    binding.button.setOnClickListener{
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}