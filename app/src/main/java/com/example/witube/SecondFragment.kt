package com.example.witube

import android.os.Bundle
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

class SecondFragment : Fragment() {

private var _binding: FragmentSecondBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        binding.videoNameText.text = title
        binding.artistNameText.text = artist
        binding.durationText.text = duration
        binding.thumbnailImage.load(thumbnail) {
            crossfade(true)
            placeholder(R.drawable.bg_thumbnail_placeholder)
            error(R.drawable.bg_thumbnail_placeholder)
        }

        binding.downloadButton.setOnClickListener {

        }



    }
override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
