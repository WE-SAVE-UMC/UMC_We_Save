package com.example.we_save.ui.accident

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.we_save.R
import com.example.we_save.common.extensions.setAppAnimation
import com.example.we_save.data.model.Accident
import com.example.we_save.databinding.FragmentAccidentEditorBinding
import com.example.we_save.databinding.ItemAccidentImageBinding
import com.example.we_save.domain.model.AccidentType
import com.example.we_save.ui.MainViewModel
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val DATA_KEY_TYPE = "TYPE"
const val REQUEST_KEY_ACCIDENT_TYPE = "REQUEST_KEY_ACCIDENT_TYPE"

class AccidentEditorFragment : Fragment() {
    companion object {
        fun getInstance(accident: Accident) = AccidentEditorFragment().apply {
            arguments = bundleOf("accident" to accident)
        }
    }

    private var accident: Accident? = null

    private var _binding: FragmentAccidentEditorBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<AccidentEditorViewModel>()

    private val headerAdapter by lazy { HeaderAdapter() }
    private val imageAdapter by lazy { ImageAdapter() }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
            if (uris.isEmpty()) return@registerForActivityResult
            viewModel.addImages(uris.map { it.toString() })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            accident = requireArguments().getParcelable("accident")
        } else if (savedInstanceState != null) {
            accident = savedInstanceState.getParcelable("accident")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("accident", accident)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccidentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_ACCIDENT_TYPE,
            viewLifecycleOwner
        ) { _, data ->
            val ordinal = data.getInt(DATA_KEY_TYPE, -1)
            if (ordinal < 0) return@setFragmentResultListener

            val type = AccidentType.entries[ordinal]
            viewModel.type.value = type
        }

        with(binding) {
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            titleEditText.addTextChangedListener {
                viewModel.title.value = it?.toString() ?: ""
            }

            locationField.setEndIconOnClickListener {
                lifecycleScope.launch {
                    locationField.setEndIconTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red_10
                            )
                        )
                    )

                    activityViewModel.updateLocation {
                        locationField.setEndIconTintList(null)
                    }
                }
            }

            typeButton.setOnClickListener {
                val fragmentManager = parentFragmentManager
                if (fragmentManager.findFragmentByTag("accident_type") != null) return@setOnClickListener

                fragmentManager.commit {
                    setAppAnimation()
                    replace(R.id.fragment_container, AccidentTypeFragment(), "accident_type")
                    addToBackStack(null)
                }
            }

            descriptionEditText.addTextChangedListener {
                viewModel.description.value = it?.toString() ?: ""
            }

            headerAdapter.onItemClickListener = {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            imageAdapter.onRemoveButtonClickListener = { position ->
                viewModel.setImages(ArrayList(imageAdapter.currentList).apply {
                    removeAt(position)
                })
            }

            imageRecyclerView.adapter = ConcatAdapter(headerAdapter, imageAdapter)

            toggle119Button.setOnClickListener {
                toggle119Button.isSelected = !toggle119Button.isSelected
            }

            registerButton.setOnClickListener {
                lifecycleScope.launch {
                    register()
                }
            }
        }

        lifecycleScope.launch {
            activityViewModel.address.collectLatest {
                if (it == null) return@collectLatest

                val elements = it.getAddressLine(0).split(" ").toMutableList()
                elements.remove(it.countryName) // 국가

                viewModel.location.value = activityViewModel.location.value
                viewModel.address.value = elements.joinToString(" ")
            }
        }

        lifecycleScope.launch {
            viewModel.address.collectLatest {
                if (it == null) return@collectLatest

                binding.locationEditText.setText(it)
            }
        }

        lifecycleScope.launch {
            viewModel.type.collectLatest {
                binding.typeTextView.isSelected = it != null
                binding.typeTextView.text = when (it) {
                    AccidentType.FIRE -> "화재"
                    AccidentType.EARTHQUAKE -> "지진"
                    AccidentType.HEAVY_RAIN -> "폭우"
                    AccidentType.HEAVY_SNOW -> "폭설"
                    AccidentType.TRAFFIC -> "교통사고"
                    null -> "선택"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.images.collectLatest {
                binding.imageCountTextView.isSelected = it.isNotEmpty()
                binding.imageCountTextView.text = "${it.count()}"

                headerAdapter.isEnabled = it.count() < 10
                imageAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.isValid.collectLatest {
                binding.registerButton.isEnabled = it
            }
        }

        accident?.let {
            binding.toolbar.title = "사건 사고 수정"

            binding.titleEditText.setText(it.title)
            binding.locationField.isVisible = false
            binding.descriptionEditText.setText(it.description)
            binding.toggle119Button.isVisible = false
            binding.registerButton.text = "수정하기"

            viewModel.type.value = it.type
            viewModel.address.value = it.address
            viewModel.images.value = it.images
        }
    }

    private fun sendSms() {
        val message = """
            |${binding.locationEditText.text?.toString() ?: ""}
            |${viewModel.title.value.trim()}
            |${viewModel.description.value.trim()}
        """.trimMargin()

        val intent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("smsto:000")  // Only SMS apps respond to this.
            putExtra("sms_body", message)
        }

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "119 신고에 실패하였습니다. 직접 신고해 주세요.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private suspend fun register() {
        if (accident == null) {
            viewModel.register()
            Toast.makeText(requireContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.update(accident!!)
            Toast.makeText(requireContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
        }

        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private class HeaderAdapter : RecyclerView.Adapter<HeaderAdapter.ItemViewHolder>() {
        var isEnabled: Boolean = true
            set(value) {
                if (field == value) return
                field = value
                notifyDataSetChanged()
            }

        var onItemClickListener: (() -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAccidentImageBinding.inflate(inflater)
            with(binding) {
                (imageView.parent as MaterialCardView).setCardBackgroundColor(
                    ContextCompat.getColor(
                        parent.context,
                        R.color.gray_05
                    )
                )
                imageView.scaleType = ImageView.ScaleType.CENTER
                imageView.setImageResource(R.drawable.ic_photo_24)

                closeButton.isInvisible = true
            }

            return ItemViewHolder(binding)
        }

        override fun getItemCount() = if (isEnabled) 1 else 0

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            (holder.binding.imageView.parent as MaterialCardView).setOnClickListener {
                onItemClickListener?.invoke()
            }
        }

        class ItemViewHolder(val binding: ItemAccidentImageBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private class ImageAdapter : ListAdapter<String, ImageAdapter.ItemViewHolder>(diffUtil) {
        companion object {
            val diffUtil = object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                    return true
                }
            }
        }

        var onRemoveButtonClickListener: ((Int) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAccidentImageBinding.inflate(inflater)
            with(binding) {
                (imageView.parent as MaterialCardView).setCardBackgroundColor(
                    ContextCompat.getColor(
                        parent.context,
                        R.color.gray_05
                    )
                )
                imageView.scaleType = ImageView.ScaleType.CENTER
                imageView.setImageResource(R.drawable.ic_photo_24)
            }

            return ItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val url = getItem(position)

            with(holder.binding) {
                Glide.with(imageView)
                    .load(url)
                    .into(imageView)

                closeButton.setOnClickListener {
                    onRemoveButtonClickListener?.invoke(holder.adapterPosition)
                }
            }
        }

        class ItemViewHolder(val binding: ItemAccidentImageBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}