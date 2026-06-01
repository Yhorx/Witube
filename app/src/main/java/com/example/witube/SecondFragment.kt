package com.example.witube

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import androidx.navigation.fragment.findNavController
import com.example.witube.databinding.FragmentSecondBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.concurrent.thread

class SecondFragment : Fragment() {

private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val apiService = ApiService()
    private var selectedFolderUri: Uri? = null

    private val folderPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) return@registerForActivityResult

        selectedFolderUri = uri
        requireContext().contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        _binding?.saveFolderEditText?.setText(uri.lastPathSegment ?: getString(R.string.preview_save_folder_default))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

      _binding = FragmentSecondBinding.inflate(inflater, container, false)
      return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        val title = arguments?.getString("title").orEmpty()
        val artist = arguments?.getString("artist").orEmpty()
        val duration = arguments?.getString("duration").orEmpty()
        val thumbnail = arguments?.getString("thumbnail").orEmpty()
        val url = arguments?.getString("url").orEmpty()

        binding.videoNameText.text = title
        binding.artistNameText.text = artist
        binding.durationText.text = duration
        binding.thumbnailImage.load(thumbnail) {
            crossfade(true)
            placeholder(R.drawable.bg_thumbnail_placeholder)
            error(R.drawable.bg_thumbnail_placeholder)
        }

        binding.chooseFolderButton.setOnClickListener {
            folderPicker.launch(downloadsTreeUri())
        }

        binding.downloadButton.setOnClickListener {
            binding.downloadButton.isEnabled = false
            binding.downloadButton.text = "Descargando..."

            thread {
                try {
                    val folderUri = selectedFolderUri
                    val resultName = if (folderUri != null) {
                        apiService.getDowloadAudioToFolder(
                            context = requireContext(),
                            ytUrl = url,
                            fileName = title,
                            folderUri = folderUri
                        ).lastPathSegment ?: title
                    } else {
                        apiService.getDowloadAudio(
                            context = requireContext(),
                            ytUrl = url,
                            fileName = title
                        ).lastPathSegment ?: title
                    }

                    requireActivity().runOnUiThread {
                        val currentBinding = _binding ?: return@runOnUiThread
                        currentBinding.downloadButton.isEnabled = true
                        currentBinding.downloadButton.text = getString(R.string.preview_download_button)
                        Snackbar.make(
                            currentBinding.root,
                            "Audio guardado: $title",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                } catch (err: Exception) {
                    requireActivity().runOnUiThread {
                        val currentBinding = _binding ?: return@runOnUiThread
                        currentBinding.downloadButton.isEnabled = true
                        currentBinding.downloadButton.text = getString(R.string.preview_download_button)
                        Snackbar.make(
                            currentBinding.root,
                            "Error al descargar audio: ${err.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }



    }
    private fun downloadsTreeUri(): Uri {
        return DocumentsContract.buildTreeDocumentUri(
            "com.android.externalstorage.documents",
            "primary:Download"
        )
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
