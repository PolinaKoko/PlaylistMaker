package com.hfad.playlistmaker.media.ui.createplaylist

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.FragmentCreatePlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var hasUnsavedChanges = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.coverImage.setImageURI(uri)
            binding.coverImage.visibility = View.VISIBLE
            binding.coverIcon.visibility = View.GONE
            hasUnsavedChanges = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameEditText.doAfterTextChanged { text ->
            binding.createButton.isEnabled = !text.isNullOrBlank()
            if (!text.isNullOrBlank()) hasUnsavedChanges = true
        }

        binding.descriptionEditText.doAfterTextChanged { text ->
            if (!text.isNullOrBlank()) hasUnsavedChanges = true
        }

        binding.coverContainer.setOnClickListener {
            pickImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.backButton.setOnClickListener {
            handleBack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBack()
        }

        binding.createButton.setOnClickListener {
            createPlaylist()
        }

        savedInstanceState?.getString(KEY_SELECTED_IMAGE_URI)?.let { uriString ->
            selectedImageUri = Uri.parse(uriString)
            binding.coverImage.setImageURI(selectedImageUri)
            binding.coverImage.visibility = View.VISIBLE
            binding.coverIcon.visibility = View.GONE
            hasUnsavedChanges = true
        }

        savedInstanceState?.getBoolean(KEY_HAS_UNSAVED_CHANGES, false)?.let { saved ->
            if (saved) hasUnsavedChanges = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SELECTED_IMAGE_URI, selectedImageUri?.toString())
        outState.putBoolean(KEY_HAS_UNSAVED_CHANGES, hasUnsavedChanges)
    }

    private fun handleBack() {
        if (hasUnsavedChanges) {
            showExitDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.finish_playlist_creation)
            .setMessage(R.string.unsaved_data_will_be_lost)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.finish) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun createPlaylist() {
        val name = binding.nameEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim().ifEmpty { null }
        val coverPath = selectedImageUri?.let { saveImageToPrivateStorage(it) }

        viewModel.createPlaylist(name, description, coverPath) { createdName ->
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created, createdName),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlists"
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

        requireContext().contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_SELECTED_IMAGE_URI = "key_selected_image_uri"
        private const val KEY_HAS_UNSAVED_CHANGES = "key_has_unsaved_changes"
    }
}