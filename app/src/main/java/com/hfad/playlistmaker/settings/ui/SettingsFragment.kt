package com.hfad.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hfad.playlistmaker.App
import com.hfad.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel {
        parametersOf(requireContext())
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.themeState.observe(viewLifecycleOwner) { settings ->
            binding.themeSwitch.isChecked = settings.isDarkTheme
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeChanged(isChecked)
            (requireContext().applicationContext as App).applyTheme()
            requireActivity().window.decorView.invalidate()
        }

        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        binding.termsButton.setOnClickListener {
            viewModel.openTerms()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}