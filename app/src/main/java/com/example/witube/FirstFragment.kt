package com.example.witube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.witube.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.concurrent.thread


class FirstFragment : Fragment() {

private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val apiService = ApiService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

      _binding = FragmentFirstBinding.inflate(inflater, container, false)
      return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.convertButton.setOnClickListener {
            val url = binding.urlEditText.text?.toString().orEmpty().trim()
            if (url.isEmpty()) {
                binding.urlInputLayout.error = getString(R.string.converter_empty_error)
                return@setOnClickListener
            }

            binding.urlInputLayout.error = null
            binding.convertButton.isEnabled = false
            binding.convertButton.text = getString(R.string.converter_loading)
            binding.conversionProgress.visibility = View.VISIBLE

            fetchAudioData(url)
        }
    }

    private fun dataToSecondFragment(info: AudioInfo, ytUrl: String) {
        requireActivity().runOnUiThread {
            val bundle = Bundle().apply {
                putString("url", ytUrl)
                putString("title", info.title)
                putString("artist", info.artists)
                putString("duration", info.duration)
                putString("thumbnail", info.thumbnail)
            }

            findNavController().navigate(
                R.id.action_FirstFragment_to_SecondFragment,
                bundle
            )
        }
    }

    private fun fetchAudioData(ytUrl: String) {
        thread {
            try {
                val audioInfo =  apiService.getAudioInfo(ytUrl)
                dataToSecondFragment(audioInfo, ytUrl)

            }catch (err: Exception){
                requireActivity().runOnUiThread {
                    val currentBinding = _binding ?: return@runOnUiThread
                    currentBinding.conversionProgress.visibility = View.GONE
                    currentBinding.convertButton.isEnabled = true
                    currentBinding.convertButton.text = getString(R.string.converter_button)
                    Snackbar.make(currentBinding.root,
                        "Error en la respuesta del servidor:${err.message}",
                        Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }



override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
