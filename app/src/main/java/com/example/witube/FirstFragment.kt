package com.example.witube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.witube.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

            binding.root.postDelayed({
                val currentBinding = _binding ?: return@postDelayed
                currentBinding.conversionProgress.visibility = View.GONE
                currentBinding.convertButton.isEnabled = true
                currentBinding.convertButton.text = getString(R.string.converter_button)
                Snackbar.make(currentBinding.root, getString(R.string.converter_started), Snackbar.LENGTH_SHORT).show()
            }, 1800)
        }
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
