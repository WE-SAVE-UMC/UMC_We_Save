package com.example.we_save.ui.accident

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.we_save.R
import com.example.we_save.common.extensions.customToast
import com.example.we_save.common.extensions.setAppAnimation
import com.example.we_save.data.model.PostDetails
import com.example.we_save.databinding.FragmentAccidentEditorBinding
import com.example.we_save.databinding.ItemAccidentImageBinding
import com.example.we_save.domain.model.AccidentType
import com.example.we_save.ui.MainViewModel
import com.example.we_save.ui.alarm.AlarmActivity
import com.example.we_save.ui.main.pages.accident.REQUEST_KEY_REFRESH
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val DATA_KEY_TYPE = "TYPE"
const val REQUEST_KEY_CATEGORY = "REQUEST_KEY_CATEGORY"

class AccidentEditorFragment : Fragment() {
    companion object {
        fun getInstance(details: PostDetails) = AccidentEditorFragment().apply {
            arguments = bundleOf("details" to details)
        }
    }

    private var details: PostDetails? = null

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
            details = requireArguments().getParcelable("details")
        } else if (savedInstanceState != null) {
            details = savedInstanceState.getParcelable("details")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("details", details)
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
            REQUEST_KEY_CATEGORY,
            viewLifecycleOwner
        ) { _, data ->
            val ordinal = data.getInt(DATA_KEY_TYPE, -1)
            if (ordinal < 0) return@setFragmentResultListener

            val type = AccidentType.entries[ordinal]
            viewModel.type.value = type
        }

        with(binding) {
            logoBar.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_notification) {
                    startActivity(Intent(requireContext(), AlarmActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    })
                }

                return@setOnMenuItemClickListener true
            }

            backButton.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            titleEditText.addTextChangedListener {
                viewModel.title.value = it?.toString() ?: ""
            }

            locationField.setEndIconOnClickListener {
                locationField.setEndIconTintList(
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_10
                        )
                    )
                )

                activityViewModel.updateAddress { address, e ->
                    if (address != null) {
                        viewModel.address.value = address
                    }

                    locationField.setEndIconTintList(null)
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
                viewModel.report119.value = !viewModel.report119.value
            }

            registerButton.setOnClickListener {
                lifecycleScope.launch {
                    register()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.address.collectLatest {
                if (it == null) return@collectLatest
                binding.locationEditText.setText(it.address)
            }
        }

        lifecycleScope.launch {
            viewModel.type.collectLatest {
                binding.typeTextView.isSelected = it != null
                binding.typeTextView.text = it?.title ?: "선택"
            }
        }

        lifecycleScope.launch {
            viewModel.report119.collectLatest {
                binding.toggle119Button.isSelected = it
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

        details?.let { details ->
            binding.titleTextView.text = "사건 사고 수정"
            binding.temporarySaveButton.isVisible = false

            binding.titleEditText.setText(details.title)
            binding.descriptionEditText.setText(details.content)
            binding.registerButton.text = "수정하기"

            viewModel.type.value = AccidentType.entries.first { it.title == details.category }
            viewModel.images.value = details.urls

            lifecycleScope.launch {
                val response = activityViewModel.getAddress(details.latitude, details.longitude)
                if (response?.results?.isNotEmpty() == true) {
                    viewModel.address.value = response
                } else {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        } ?: run {
            binding.temporarySaveButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.temporarySave()

                    //requireActivity().applicationContext.customToast("임시 저장되었습니다.").show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

            lifecycleScope.launch {
                val savedData = viewModel.getSavedData()

                if (savedData != null) {
                    val request = savedData.first
                    val images = savedData.second

                    binding.titleEditText.setText(request.title)
                    binding.descriptionEditText.setText(request.content)

                    viewModel.type.value =
                        AccidentType.entries.firstOrNull { it.name == request.category }
                    viewModel.report119.value = request.report119
                    viewModel.images.value = images

                    if (request.latitude != null && request.longitude != null) {
                        val response =
                            activityViewModel.getAddress(
                                request.latitude.toDouble(),
                                request.longitude.toDouble()
                            )
                        if (response?.results?.isNotEmpty() == true) {
                            viewModel.address.value = response
                            return@launch
                        }
                    }
                }

                viewModel.address.value = activityViewModel.address.value
            }
        }
    }

    private suspend fun register() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(LayoutInflater.from(requireContext()).inflate(R.layout.dialog_uploading, null))
            .create()
            .apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }

        dialog.show()

        if (details == null) {
            val response = viewModel.register()
            Log.d("Editor", response.body()?.message ?: "null")

            // 사건 사고 목록 갱신용
            setFragmentResult(REQUEST_KEY_REFRESH, bundleOf())

            //requireContext().customToast("등록되었습니다.").show()
        } else {
            val response = viewModel.update(details!!)
            Log.d("Editor", response.body()?.message ?: "null")

            //requireContext().customToast("수정되었습니다.").show()
        }

        dialog.dismiss()

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