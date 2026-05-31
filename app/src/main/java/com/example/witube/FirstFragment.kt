package com.example.witube

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.witube.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import kotlin.concurrent.thread
import okhttp3.Request
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class FirstFragment : Fragment() {

private var _binding: FragmentFirstBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val client = OkHttpClient()

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

    private fun dataToSecondFragment(title: String, artists: String, duration: String, thumbnail: String) {
        requireActivity().runOnUiThread {
            val bundle = Bundle().apply {
                putString("title", title)
                putString("artist", artists)
                putString("duration", duration)
                putString("thumbnail", thumbnail)
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
                val jsonBody = JSONObject()
                    .put("url", ytUrl)
                    .toString()

                val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("http://192.168.100.234:3000/info-audio")
                    .post(requestBody)
                    .build()

                val jsonText = client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw Exception("Respuesta del servidor: ${response.code}")
                    }

                    response.body?.string().orEmpty()
                }

                val json = JSONObject(jsonText)

                val title = json.getString("title")
                val artist = json.getString("channel")
                val duration = json.getString("duration_string")
                val thumbnail = json.getString("thumbnail")

                dataToSecondFragment(title, artist, duration, thumbnail)

            }catch (err: Exception){
                requireActivity().runOnUiThread {
                    val currentBinding = _binding ?: return@runOnUiThread
                    currentBinding.conversionProgress.visibility = View.GONE
                    currentBinding.convertButton.isEnabled = true
                    currentBinding.convertButton.text = getString(R.string.converter_button)
                    Snackbar.make(currentBinding.root,
                        "Error al obtener datos",
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
