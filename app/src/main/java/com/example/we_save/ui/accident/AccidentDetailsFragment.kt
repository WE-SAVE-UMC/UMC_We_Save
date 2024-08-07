package com.example.we_save.ui.accident

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.we_save.R
import com.example.we_save.common.extensions.hideKeyboard
import com.example.we_save.common.extensions.setAppAnimation
import com.example.we_save.data.model.Accident
import com.example.we_save.data.model.AccidentWithComments
import com.example.we_save.data.model.Comment
import com.example.we_save.databinding.DialogAlertBinding
import com.example.we_save.databinding.FragmentAccidentDetailsBinding
import com.example.we_save.databinding.ItemAccidentDetailsCommentBinding
import com.example.we_save.databinding.ItemAccidentDetailsFooterBinding
import com.example.we_save.databinding.ItemAccidentDetailsHeaderBinding
import com.example.we_save.databinding.ItemCommentImageBinding
import com.example.we_save.databinding.ItemRepliedImageBinding
import com.example.we_save.databinding.ItemReplyingImageBinding
import com.example.we_save.domain.model.AccidentType
import com.example.we_save.ui.MainViewModel
import com.example.we_save.ui.accident.bottomsheet.ImagePickerBottomSheet
import com.example.we_save.ui.accident.bottomsheet.MyAccidentBottomSheet
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.min

const val DATA_KEY_EVENT = "EVENT"
const val REQUEST_KEY_BOTTOM_SHEET_EVENT = "REQUEST_KEY_BOTTOM_SHEET_EVENT"
const val VALID_RADIUS_METER = 1500

enum class AccidentBottomSheetEvent {
    SITUATION_END, EDIT, REMOVE, REPORT, SHARE, BLOCK, ALBUM, CAMERA
}

enum class ParentFragment {
    NEAR, DOMESTIC
}

class AccidentDetailsFragment : Fragment() {
    companion object {
        fun getInstance(accident: Accident, parent: ParentFragment) =
            AccidentDetailsFragment().apply {
                arguments = bundleOf("accident" to accident, "from" to parent.name)
            }
    }

    private lateinit var accident: Accident
    private lateinit var from: ParentFragment

    private var _binding: FragmentAccidentDetailsBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<AccidentDetailsViewModel>()

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) return@registerForActivityResult
            takePicture()
        }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            if (uris.isEmpty()) return@registerForActivityResult
            viewModel.addReplyImages(uris.map { it.toString() })
        }

    private var pendingCameraMediaUri: Uri? = null

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (!it) return@registerForActivityResult

        viewModel.addReplyImages(listOf(pendingCameraMediaUri!!.toString()))
        pendingCameraMediaUri = null
    }

    private val headerAdapter by lazy { HeaderAdapter() }
    private val footerAdapter by lazy { FooterAdapter() }
    private val commentAdapter by lazy { CommentAdapter() }
    private val replyingImageAdapter by lazy { ReplyingImageAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            accident = requireArguments().getParcelable("accident")!!
            from = ParentFragment.valueOf(requireArguments().getString("from")!!)
        } else if (savedInstanceState != null) {
            accident = savedInstanceState.getParcelable("accident")!!
            from = ParentFragment.valueOf(savedInstanceState.getString("from")!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("accident", accident)
        outState.putString("from", from.name)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccidentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.setFragmentResultListener(
            REQUEST_KEY_BOTTOM_SHEET_EVENT,
            viewLifecycleOwner
        ) { _, data ->
            val ordinal = data.getInt(DATA_KEY_EVENT, -1)
            if (ordinal < 0) return@setFragmentResultListener

            val event = AccidentBottomSheetEvent.entries[ordinal]
            onReceiveBottomSheetEvent(event)
        }

        if (from == ParentFragment.DOMESTIC) {
            activityViewModel.location.value?.let {
                val results = FloatArray(1)
                Location.distanceBetween(
                    it.latitude,
                    it.longitude,
                    accident.lat,
                    accident.lng,
                    results
                )
                results.getOrNull(0)?.let { distance ->
                    footerAdapter.distance = distance.toDouble()

                    if (distance > VALID_RADIUS_METER) {
                        with(binding) {
                            replyContainer.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.gray_05
                                )
                            )
                            replyField.isEnabled = false
                            replyEditText.hint = "현 위치에서는 작성이 불가합니다."
                            addButton.isEnabled = false
                            sendButton.isEnabled = false
                        }
                    }
                }
            }
        } else {
            footerAdapter.distance = 0.0
        }

        with(binding) {
            toolbar.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            headerAdapter.onOverflowButtonClickListener = {
                MyAccidentBottomSheet().show(childFragmentManager, "bottom_sheet")
            }

            recyclerView.adapter = ConcatAdapter(headerAdapter, footerAdapter, commentAdapter)

            replyImageRecyclerView.adapter = replyingImageAdapter.apply {
                onRemoveButtonClickListener = { position ->
                    viewModel.setReplyImages(ArrayList(replyingImageAdapter.currentList).apply {
                        removeAt(position)
                    })
                }
            }

            replyEditText.addTextChangedListener {
                viewModel.replyMessage.value = it?.toString()?.trim() ?: ""
            }

            addButton.setOnClickListener {
                hideKeyboard(replyEditText)

                val results = IntArray(2)
                replyContainer.getLocationOnScreen(results)
                val padding = resources.displayMetrics.heightPixels - results[1]

                val bottomSheet = ImagePickerBottomSheet().apply {
                    arguments = bundleOf("padding" to padding)
                }
                bottomSheet.lifecycle.addObserver(LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> binding.addButton.setImageResource(R.drawable.ic_filled_circle_plus_24)
                        Lifecycle.Event.ON_PAUSE -> (viewModel.replyImage.value.isNotEmpty()).let {
                            binding.addButton.setImageResource(if (it) R.drawable.ic_filled_circle_plus_24 else R.drawable.ic_circle_plus_24)
                        }

                        else -> return@LifecycleEventObserver
                    }
                })

                bottomSheet.show(childFragmentManager, "image_picker")
            }

            sendButton.setOnClickListener {
                hideKeyboard(replyEditText)

                lifecycleScope.launch {
                    sendReply()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.replyMessage.collectLatest {
                with(binding.sendButton) {
                    if (it.isNotEmpty()) {
                        isEnabled = true
                        setColorFilter(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red_10
                            )
                        )
                    } else {
                        isEnabled = false
                        clearColorFilter()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.replyImage.collectLatest {
                binding.replyImageRecyclerView.isVisible = it.isNotEmpty()

                replyingImageAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.replyImage.collectLatest {
                binding.addButton.setImageResource(if (it.isNotEmpty()) R.drawable.ic_filled_circle_plus_24 else R.drawable.ic_circle_plus_24)
            }
        }

        lifecycleScope.launch {
            viewModel.getAccidentWithComments(accident).collectLatest {
                if (it == null) {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    return@collectLatest
                }

                binding.toolbar.title = it.accident.title
                headerAdapter.submitList(listOf(it.accident))
                footerAdapter.submitList(listOf(it))
                commentAdapter.submitList(it.comments.sortedBy { it.timestamp })
            }
        }
    }

    private fun takePicture() {
        try {
            val dir = requireContext().cacheDir
            dir.mkdirs()
            val file = File.createTempFile(UUID.randomUUID().toString(), ".jpg", dir)

            pendingCameraMediaUri = Uri.fromFile(file)

            val imageUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.we_save.image_provider",
                file
            )

            takePicture.launch(imageUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun sendReply() {
        viewModel.send()

        binding.replyEditText.setText("")
    }

    private fun onReceiveBottomSheetEvent(event: AccidentBottomSheetEvent) {
        when (event) {
            AccidentBottomSheetEvent.SITUATION_END -> {
                lifecycleScope.launch {
                    viewModel.endSituation()
                    Toast.makeText(requireContext(), "상황 종료", Toast.LENGTH_SHORT).show()
                }
            }

            AccidentBottomSheetEvent.EDIT -> {
                val fragmentManager = requireActivity().supportFragmentManager
                val accident = headerAdapter.currentList.firstOrNull()

                if (fragmentManager.findFragmentByTag("accident_editor") == null && accident != null) {
                    fragmentManager.commit {
                        setAppAnimation()
                        replace(
                            R.id.fragment_container,
                            AccidentEditorFragment.getInstance(accident),
                            "accident_editor"
                        )
                        addToBackStack(null)
                    }
                }
            }

            AccidentBottomSheetEvent.REMOVE -> {
                lifecycleScope.launch {
                    viewModel.remove()
                    Toast.makeText(requireContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            AccidentBottomSheetEvent.REPORT -> TODO()
            AccidentBottomSheetEvent.SHARE -> TODO()
            AccidentBottomSheetEvent.BLOCK -> TODO()
            AccidentBottomSheetEvent.ALBUM -> {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            AccidentBottomSheetEvent.CAMERA -> {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission.launch(Manifest.permission.CAMERA)
                    return
                }

                takePicture()
            }
        }
    }

    private fun showAlertDialog(
        title: String,
        message: String,
        annotation: String,
        callback: (suspend () -> Unit)? = null
    ) {
        if (title.isBlank() && message.isBlank() && annotation.isBlank()) return

        val binding = DialogAlertBinding.inflate(LayoutInflater.from(requireContext()))

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()

        with(binding) {
            if (title.isBlank()) {
                titleTextView.isVisible = false
            } else {
                titleTextView.text = title.trim()
            }

            if (message.isBlank()) {
                messageTextView.isVisible = false
            } else {
                messageTextView.text = message.trim()
            }

            if (annotation.isBlank()) {
                annotationTextView.isVisible = false
            } else {
                annotationTextView.text = annotation.trim()
            }

            negativeButton.setOnClickListener { dialog.dismiss() }
            positiveButton.setOnClickListener {
                dialog.dismiss()

                lifecycleScope.launch {
                    callback?.invoke()
                }
            }
        }

        dialog.show()
    }

    private fun share() {
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, "Introducing content previews")
            putExtra(Intent.EXTRA_TEXT, "https://developer.android.com/training/sharing/")
            data = Uri.parse(replyingImageAdapter.currentList.first())
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }, null)

        startActivity(share)
    }

    private class ReplyingImageAdapter :
        ListAdapter<String, ReplyingImageAdapter.ReplyingImageItemViewHolder>(diffUtil) {

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

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ReplyingImageItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemReplyingImageBinding.inflate(inflater, parent, false)
            return ReplyingImageItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ReplyingImageItemViewHolder, position: Int) {
            with(holder.binding) {
                Glide.with(imageView)
                    .load(getItem(position))
                    .into(imageView)

                closeButton.setOnClickListener {
                    onRemoveButtonClickListener?.invoke(holder.adapterPosition)
                }
            }
        }

        class ReplyingImageItemViewHolder(val binding: ItemReplyingImageBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private class HeaderAdapter : ListAdapter<Accident, HeaderAdapter.HeaderItemViewHolder>(
        diffUtil
    ) {
        companion object {
            val diffUtil = object : DiffUtil.ItemCallback<Accident>() {
                override fun areItemsTheSame(oldItem: Accident, newItem: Accident): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Accident, newItem: Accident): Boolean {
                    return oldItem.lat == newItem.lat &&
                            oldItem.lng == newItem.lng &&
                            oldItem.address == newItem.address &&
                            oldItem.title == newItem.title &&
                            oldItem.description == newItem.description &&
                            oldItem.type == newItem.type &&
                            oldItem.images.count() == newItem.images.count() &&
                            oldItem.images.zip(newItem.images).none { it.first != it.second }
                }
            }
        }

        var onOverflowButtonClickListener: ((Accident) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAccidentDetailsHeaderBinding.inflate(inflater, parent, false)
            return HeaderItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: HeaderItemViewHolder, position: Int) {
            val item = getItem(position)
            with(holder.binding) {
                userNameTextView.text = "김재난"
                timestampTextView.text =
                    SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.US).format(item.timestamp)
                overflowButton.setOnClickListener {
                    onOverflowButtonClickListener?.invoke(item)
                }

                descriptionTextView.text = item.description
                imageTotalPageCountTextView.text = "/${item.images.count()}"
                imageCurrentPageTextView.text = "1"

                viewPager.adapter = ImageViewPagerAdapter(item.images)

                if (viewPager.tag is ViewPager2.OnPageChangeCallback) {
                    viewPager.unregisterOnPageChangeCallback(viewPager.tag as ViewPager2.OnPageChangeCallback)
                }

                val callback = object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)

                        imageCurrentPageTextView.text = "${position + 1}"
                    }
                }

                viewPager.tag = callback
                viewPager.registerOnPageChangeCallback(callback)

                imageViewContainer.isVisible = item.images.isNotEmpty()

                val id = when (item.type) {
                    AccidentType.FIRE -> R.drawable.ic_fire_16
                    AccidentType.EARTHQUAKE -> R.drawable.ic_heartbeat_16
                    AccidentType.HEAVY_RAIN -> R.drawable.ic_droplet_16
                    AccidentType.HEAVY_SNOW -> R.drawable.ic_snowflake_16
                    AccidentType.TRAFFIC -> R.drawable.ic_car_16
                    AccidentType.ETC -> R.drawable.ic_etc_16
                }

                typeTextView.text = item.type.title
                typeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(id, 0, 0, 0)

                locationTextView.text = item.address
            }
        }

        class HeaderItemViewHolder(val binding: ItemAccidentDetailsHeaderBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private class ImageViewPagerAdapter(private val images: List<String>) :
        RecyclerView.Adapter<ImageViewPagerAdapter.ImageItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
            val imageView = ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            return ImageItemViewHolder(imageView)
        }

        override fun getItemCount() = images.count()

        override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
            val url = images[position]

            Glide.with(holder.itemView)
                .load(url)
                .into(holder.itemView as ImageView)
        }

        class ImageItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

    private class FooterAdapter :
        ListAdapter<AccidentWithComments, FooterAdapter.FooterItemViewHolder>(
            diffUtil
        ) {
        companion object {
            val diffUtil = object : DiffUtil.ItemCallback<AccidentWithComments>() {
                override fun areItemsTheSame(
                    oldItem: AccidentWithComments,
                    newItem: AccidentWithComments
                ): Boolean {
                    return true
                }

                override fun areContentsTheSame(
                    oldItem: AccidentWithComments,
                    newItem: AccidentWithComments
                ): Boolean {
                    return oldItem.comments.count() == newItem.comments.count()
                }
            }
        }

        // Meter
        var distance: Double? = null
            set(value) {
                if (field == value) return
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAccidentDetailsFooterBinding.inflate(inflater, parent, false)
            return FooterItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FooterItemViewHolder, position: Int) {
            val item = getItem(position)

            with(holder.binding) {
                replyCountTextView.text = "${item.comments.count()}"
                imageCountTextView.text = "${item.comments.sumOf { it.images.count() }}"
                recyclerView.adapter = RepliedImageAdapter(item.comments.flatMap { it.images })
                noResultTextView.isVisible = item.comments.isEmpty()

                warningIndicator.isVisible = (distance ?: Double.MAX_VALUE) > VALID_RADIUS_METER
            }
        }

        class FooterItemViewHolder(val binding: ItemAccidentDetailsFooterBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private class RepliedImageAdapter(private val images: List<String>) :
        RecyclerView.Adapter<RepliedImageAdapter.RepliedImageItemViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RepliedImageItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemRepliedImageBinding.inflate(inflater, parent, false)
            return RepliedImageItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RepliedImageItemViewHolder, position: Int) {
            val url = images[position]

            with(holder.binding) {
                if (position == 4 && images.count() > 5) {
                    imageView.setImageDrawable(ColorDrawable(Color.BLACK))
                    textView.text = "+${images.count() - 4}"
                } else {
                    Glide.with(imageView)
                        .load(url)
                        .into(imageView)

                    textView.text = ""
                }
            }
        }

        override fun getItemCount(): Int {
            return min(images.count(), 5)
        }

        class RepliedImageItemViewHolder(val binding: ItemRepliedImageBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private class CommentAdapter : ListAdapter<Comment, CommentAdapter.CommentItemViewHolder>(
        diffUtil
    ) {
        companion object {
            val diffUtil = object : DiffUtil.ItemCallback<Comment>() {
                override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                    return oldItem.message == newItem.message
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAccidentDetailsCommentBinding.inflate(inflater, parent, false)
            return CommentItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: CommentItemViewHolder, position: Int) {
            val item = getItem(position)

            with(holder.binding) {
                userNameTextView.text = "김재난"
                agoTextView.text = PrettyTime().format(Date(item.timestamp))
                contentTextView.text = item.message

                recyclerView.isVisible = item.images.isNotEmpty()
                recyclerView.adapter = CommentImageAdapter(item.images)
            }
        }

        class CommentItemViewHolder(val binding: ItemAccidentDetailsCommentBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private class CommentImageAdapter(private val images: List<String>) :
        RecyclerView.Adapter<CommentImageAdapter.CommentImageItemViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CommentImageItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCommentImageBinding.inflate(inflater, parent, false)
            return CommentImageItemViewHolder(binding)
        }

        override fun getItemCount() = images.count()

        override fun onBindViewHolder(holder: CommentImageItemViewHolder, position: Int) {
            val url = images[position]

            with(holder.binding) {
                Glide.with(imageView)
                    .load(url)
                    .into(imageView)
            }
        }

        class CommentImageItemViewHolder(val binding: ItemCommentImageBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}