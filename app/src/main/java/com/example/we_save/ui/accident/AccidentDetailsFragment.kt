package com.example.we_save.ui.accident

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.we_save.R
import com.example.we_save.common.Constants.dateFormat
import com.example.we_save.common.extensions.customToast
import com.example.we_save.common.extensions.hideKeyboard
import com.example.we_save.common.extensions.setAppAnimation
import com.example.we_save.data.model.BaseResponse
import com.example.we_save.data.model.PostComment
import com.example.we_save.data.model.PostDetails
import com.example.we_save.databinding.DialogAlertBinding
import com.example.we_save.databinding.FragmentAccidentDetailsBinding
import com.example.we_save.databinding.ItemAccidentDetailsCommentBinding
import com.example.we_save.databinding.ItemAccidentDetailsFooterBinding
import com.example.we_save.databinding.ItemAccidentDetailsHeaderBinding
import com.example.we_save.databinding.ItemCommentImageBinding
import com.example.we_save.databinding.ItemRepliedImageBinding
import com.example.we_save.databinding.ItemReplyingImageBinding
import com.example.we_save.databinding.PopupCommentBinding
import com.example.we_save.domain.model.AccidentType
import com.example.we_save.ui.accident.bottomsheet.ImagePickerBottomSheet
import com.example.we_save.ui.accident.bottomsheet.MyAccidentBottomSheet
import com.example.we_save.ui.accident.bottomsheet.YourAccidentBottomSheet
import com.example.we_save.ui.alarm.AlarmActivity
import com.example.we_save.ui.main.pages.accident.DATA_KEY_POST_RESULT
import com.example.we_save.ui.main.pages.accident.REQUEST_KEY_UPDATE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import kotlin.math.min

const val DATA_KEY_EVENT = "EVENT"
const val REQUEST_KEY_BOTTOM_SHEET_EVENT = "REQUEST_KEY_BOTTOM_SHEET_EVENT"

enum class AccidentBottomSheetEvent {
    SITUATION_END, EDIT, REMOVE, REPORT, SHARE, BLOCK, ALBUM, CAMERA
}

class AccidentDetailsFragment : Fragment() {
    companion object {
        fun getInstance(postId: Long) =
            AccidentDetailsFragment().apply {
                arguments = bundleOf("postId" to postId)
            }
    }

    private var _binding: FragmentAccidentDetailsBinding? = null
    private val binding get() = _binding!!

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

    private var editingCommentId: Long? = null
        set(value) {
            val comments = ArrayList(viewModel.details.value?.result?.commentsList ?: listOf())
            val index = comments.indexOfFirst { it.id == value }

            comments.forEachIndexed { index, comment ->
                val isEditing = value == comment.id
                if (comment.isEditing != isEditing) {
                    comments[index] = comment.copy().apply { this.isEditing = isEditing }
                }
            }

            if (field != value && value != null && index >= 0) {
                viewModel.replyMessage.value = comments[index].content
                binding.replyEditText.setText(comments[index].content)
                viewModel.setReplyImages(comments[index].urls)
                onBackPressedCallback.isEnabled = true

            } else if (value == null) {
                viewModel.replyMessage.value = ""
                viewModel.setReplyImages(listOf())
                binding.replyEditText.setText("")
                onBackPressedCallback.isEnabled = true
            }

            commentAdapter.submitList(comments)
            field = value
        }

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (isEnabled) {
                if (editingCommentId != null &&
                    (viewModel.details.value?.result?.commentsList?.indexOfFirst { it.id == editingCommentId!! }
                        ?: -1) < 0
                ) {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                } else if (editingCommentId == null) {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

            editingCommentId = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var postId = 0L
        if (arguments != null) {
            postId = requireArguments().getLong("postId")
        }
        if (savedInstanceState != null) {
            postId = savedInstanceState.getLong("postId")
        }

        assert(postId != 0L)

        viewModel.postId = postId
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("postId", viewModel.postId)
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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        childFragmentManager.setFragmentResultListener(
            REQUEST_KEY_BOTTOM_SHEET_EVENT,
            viewLifecycleOwner
        ) { _, data ->
            val ordinal = data.getInt(DATA_KEY_EVENT, -1)
            if (ordinal < 0) return@setFragmentResultListener

            val event = AccidentBottomSheetEvent.entries[ordinal]
            onReceiveBottomSheetEvent(event)
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

            headerAdapter.apply {
                onOverflowButtonClickListener = {
                    if (childFragmentManager.findFragmentByTag("bottom_sheet") == null) {
                        if (viewModel.isMine) {
                            MyAccidentBottomSheet().apply {
                                arguments = bundleOf("details" to viewModel.details.value?.result)
                            }.show(childFragmentManager, "bottom_sheet")
                        } else {
                            YourAccidentBottomSheet().show(childFragmentManager, "bottom_sheet")
                        }
                    }
                }

                onLikeButtonClickListener = {
                    lifecycleScope.launch {
                        try {
                            val response = viewModel.like()
                            if (response.body()?.isSuccess == false) {
                                requireContext().applicationContext.customToast(response.body()!!.message)
                                    .show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                onDislikeButtonClickListener = {
                    lifecycleScope.launch {
                        try {
                            val response = viewModel.dislike()
                            if (response.body()?.isSuccess == false) {
                                requireContext().applicationContext.customToast(response.body()!!.message)
                                    .show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            commentAdapter.apply {
                myUserId = viewModel.myUserId

                onEditButtonClickListener = {
                    editingCommentId = it.id
                }

                onReportButtonClickListener = {
                    showAlertDialog(
                        "신고하기",
                        "'${it.nickname}'님을 신고하시겠습니까?",
                        "허위신고 시 불이익을 받을 수 있습니다."
                    ) {
                        lifecycleScope.launch {
                            try {
                                val response = viewModel.reportComment(it.id)
                                response.body()?.message?.let { message ->
                                    requireContext().applicationContext.customToast(message).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                onRemoveButtonClickListener = {
                    lifecycleScope.launch {
                        try {
                            val response = viewModel.removeComment(it.id)
                            response.body()?.message?.let { message ->
                                requireContext().applicationContext.customToast(message).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
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
                        Lifecycle.Event.ON_PAUSE -> (viewModel.replyImages.value.isNotEmpty()).let {
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
                    val dialog = MaterialAlertDialogBuilder(requireContext())
                        .setView(
                            LayoutInflater.from(requireContext())
                                .inflate(R.layout.dialog_uploading, null)
                        )
                        .create()
                        .apply {
                            setCancelable(false)
                            setCanceledOnTouchOutside(false)
                        }

                    dialog.show()

                    sendReply()

                    dialog.dismiss()
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
            viewModel.replyImages.collectLatest {
                binding.replyImageRecyclerView.isVisible = it.isNotEmpty()

                replyingImageAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.replyImages.collectLatest {
                binding.addButton.setImageResource(if (it.isNotEmpty()) R.drawable.ic_filled_circle_plus_24 else R.drawable.ic_circle_plus_24)
            }
        }

        lifecycleScope.launch {
            var isFirst = true

            viewModel.details.collectLatest {
                if (it == null) return@collectLatest

                if (!it.isSuccess) {
                    requireContext().customToast(it.message).show()

                    setFragmentResult(
                        REQUEST_KEY_UPDATE,
                        bundleOf(DATA_KEY_POST_RESULT to viewModel.postId)
                    )
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    return@collectLatest
                }

                val details = it.result!!

                // 사건 사고 목록 업데이트용
                setFragmentResult(
                    REQUEST_KEY_UPDATE,
                    bundleOf(DATA_KEY_POST_RESULT to details.postDto)
                )

                binding.titleTextView.text = details.title
                headerAdapter.submitList(listOf(details))
                footerAdapter.submitList(listOf(details))
                commentAdapter.submitList(details.commentsList.map {
                    it.isEditing = (it.id == editingCommentId)
                    it
                }.let {
                    if (editingCommentId != null && it.none { it.isEditing }) {
                        editingCommentId = null
                    }

                    it
                }) {
                    if (isFirst) {
                        isFirst = false
                        (binding.recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                            0,
                            0
                        )
                    }
                }
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
        viewModel.send(editingCommentId)

        binding.replyEditText.setText("")
        editingCommentId = null
    }

    private fun onReceiveBottomSheetEvent(event: AccidentBottomSheetEvent) {
        when (event) {
            AccidentBottomSheetEvent.SITUATION_END -> {
                lifecycleScope.launch {
                    viewModel.complete()
                    requireContext().customToast("상황 종료처리 되었습니다.").show()
                }
            }

            AccidentBottomSheetEvent.EDIT -> {
                val fragmentManager = requireActivity().supportFragmentManager
                val details = viewModel.details.value?.result ?: return

                if (fragmentManager.findFragmentByTag("accident_editor") == null) {
                    fragmentManager.commit {
                        setAppAnimation()
                        replace(
                            R.id.fragment_container,
                            AccidentEditorFragment.getInstance(details),
                            "accident_editor"
                        )
                        addToBackStack(null)
                    }
                }
            }

            AccidentBottomSheetEvent.REMOVE -> {
                lifecycleScope.launch {
                    viewModel.remove()
                    requireContext().customToast("삭제되었습니다.").show()

                    setFragmentResult(
                        REQUEST_KEY_UPDATE,
                        bundleOf(DATA_KEY_POST_RESULT to viewModel.postId)
                    )
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }

            AccidentBottomSheetEvent.REPORT -> {
                showAlertDialog(
                    "신고하기",
                    "'${viewModel.details.value?.result?.nickname}'님을 신고하시겠습니까?",
                    "허위신고 시 불이익을 받을 수 있습니다."
                ) {
                    lifecycleScope.launch {
                        try {
                            val response = viewModel.report()
                            response.body()?.message?.let { message ->
                                requireContext().applicationContext.customToast(message).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            AccidentBottomSheetEvent.SHARE -> share()

            AccidentBottomSheetEvent.BLOCK -> {
                showAlertDialog(
                    "차단하기",
                    "'${viewModel.details.value?.result?.nickname}'님을 차단하시겠습니까?",
                    "마이페이지-차단관리에서 차단해제 하실 수 있습니다."
                ) {
                    lifecycleScope.launch {
                        try {
                            val response = viewModel.block()
                            if (response.isSuccessful) {
                                requireContext().applicationContext.customToast("차단되었습니다.").show()
                            } else {
                                val json = response.errorBody()?.string()
                                val response = Gson().fromJson(json, BaseResponse::class.java)
                                requireContext().applicationContext.customToast(response.message)
                                    .show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

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
        val details = viewModel.details.value?.result ?: return

        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, details.title)
            putExtra(Intent.EXTRA_TEXT, details.content)
            // data = Uri.parse(replyingImageAdapter.currentList.first())
            // flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }, null)

        startActivity(share)
    }

    override fun onResume() {
        super.onResume()

        viewModel.refresh()
    }
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

private class HeaderAdapter : ListAdapter<PostDetails, HeaderAdapter.HeaderItemViewHolder>(
    diffUtil
) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostDetails>() {
            override fun areItemsTheSame(oldItem: PostDetails, newItem: PostDetails): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PostDetails, newItem: PostDetails): Boolean {
                return oldItem.category == newItem.category &&
                        oldItem.title == newItem.title &&
                        oldItem.content == newItem.content &&
                        oldItem.postRegionName == newItem.postRegionName &&
                        oldItem.userReaction == newItem.userReaction &&
                        oldItem.hearts == newItem.hearts &&
                        oldItem.dislikes == newItem.dislikes &&
                        oldItem.imageCount == newItem.imageCount &&
                        oldItem.images.size == newItem.images.size &&
                        oldItem.images.zip(newItem.images).none { it.first != it.second }
            }

            override fun getChangePayload(oldItem: PostDetails, newItem: PostDetails): Any {
                return Any()
            }
        }
    }

    var onOverflowButtonClickListener: ((PostDetails) -> Unit)? = null
    var onLikeButtonClickListener: (() -> Unit)? = null
    var onDislikeButtonClickListener: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAccidentDetailsHeaderBinding.inflate(inflater, parent, false)
        return HeaderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderItemViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            Glide.with(avatarImageView)
                .load(item.avatar)
                .error(R.drawable.ic_profile_32)
                .placeholder(R.drawable.ic_profile_32)
                .into(avatarImageView)

            userNameTextView.text = item.nickname
            timestampTextView.text =
                SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(
                    dateFormat.parse(item.createdAt)
                )

            overflowButton.setOnClickListener {
                onOverflowButtonClickListener?.invoke(item)
            }

            descriptionTextView.text = item.content

            let {
                if (viewPager.adapter is ImageViewPagerAdapter) {
                    val adapter = viewPager.adapter as ImageViewPagerAdapter
                    if (adapter.images.size == item.urls.size && adapter.images.zip(item.urls)
                            .none { it.first != it.second }
                    ) {
                        return@let false
                    }
                }

                return@let true
            }.let { isChanged ->
                if (!isChanged) return@let

                //region Image
                imageTotalPageCountTextView.text = "/${item.urls.count()}"
                imageCurrentPageTextView.text = "1"

                viewPager.adapter = ImageViewPagerAdapter(item.urls)

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

                imageViewContainer.isVisible = item.urls.isNotEmpty()
                //endregion
            }

            val id = when (item.category) {
                AccidentType.FIRE.title -> R.drawable.ic_fire_16
                AccidentType.EARTHQUAKE.title -> R.drawable.ic_heartbeat_16
                AccidentType.HEAVY_RAIN.title -> R.drawable.ic_droplet_16
                AccidentType.HEAVY_SNOW.title -> R.drawable.ic_snowflake_16
                AccidentType.TRAFFIC_ACCIDENT.title -> R.drawable.ic_car_16
                else -> R.drawable.ic_etc_16
            }

            typeTextView.text = item.category
            typeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(id, 0, 0, 0)

            locationTextView.text = item.postRegionName

            checkCountTextView.text = item.hearts.toString()
            falsehoodCountTextView.text = item.dislikes.toString()

            checkButton.isSelected = false
            falsehoodButton.isSelected = false

            if (item.userReaction == true) {
                checkButton.isSelected = true
            } else if (item.userReaction == false) {
                falsehoodButton.isSelected = true
            }

            checkButton.setOnClickListener {
                onLikeButtonClickListener?.invoke()
            }

            falsehoodButton.setOnClickListener {
                onDislikeButtonClickListener?.invoke()
            }
        }
    }

    class HeaderItemViewHolder(val binding: ItemAccidentDetailsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)
}

private class ImageViewPagerAdapter(val images: List<String>) :
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
    ListAdapter<PostDetails, FooterAdapter.FooterItemViewHolder>(
        diffUtil
    ) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostDetails>() {
            override fun areItemsTheSame(
                oldItem: PostDetails,
                newItem: PostDetails
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PostDetails,
                newItem: PostDetails
            ): Boolean {
                return oldItem.commentsList.size == newItem.commentsList.size &&
                        oldItem.commentsList.zip(newItem.commentsList).none {
                            it.first.id != it.second.id ||
                                    it.first.urls.zip(it.second.urls).any { it.first != it.second }
                        }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAccidentDetailsFooterBinding.inflate(inflater, parent, false)
        return FooterItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FooterItemViewHolder, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            replyCountTextView.text = "${item.commentsList.size}"
            imageCountTextView.text = "${item.commentsList.sumOf { it.urls.count() }}"
            recyclerView.adapter = RepliedImageAdapter(item.commentsList.flatMap { it.urls })
            noResultTextView.isVisible = item.commentsList.isEmpty()

            warningIndicator.isVisible = false
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
            if (position == 5 && images.count() > 6) {
                imageView.setImageDrawable(ColorDrawable(Color.BLACK))
                textView.text = "+${images.count() - 5}"
            } else {
                Glide.with(imageView)
                    .load(url)
                    .into(imageView)

                textView.text = ""
            }
        }
    }

    override fun getItemCount(): Int {
        return min(images.count(), 6)
    }

    class RepliedImageItemViewHolder(val binding: ItemRepliedImageBinding) :
        RecyclerView.ViewHolder(binding.root)
}

private class CommentAdapter : ListAdapter<PostComment, CommentAdapter.CommentItemViewHolder>(
    diffUtil
) {
    var myUserId: Long = 0L

    var onReportButtonClickListener: ((PostComment) -> Unit)? = null
    var onEditButtonClickListener: ((PostComment) -> Unit)? = null
    var onRemoveButtonClickListener: ((PostComment) -> Unit)? = null

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PostComment>() {
            override fun areItemsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PostComment, newItem: PostComment): Boolean {
                return oldItem.content == newItem.content &&
                        oldItem.createdAt == newItem.createdAt &&
                        oldItem.isEditing == newItem.isEditing &&
                        oldItem.urls.size == newItem.urls.size &&
                        oldItem.urls.zip(newItem.urls).none { it.first != it.second }
            }

            override fun getChangePayload(oldItem: PostComment, newItem: PostComment): Any {
                return Any()
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
            contentContainer.setBackgroundColor(if (item.isEditing) Color.parseColor("#fff6f6f6") else Color.TRANSPARENT)

            Glide.with(avatarImageView)
                .load(item.avatar)
                .error(R.drawable.ic_profile_32)
                .placeholder(R.drawable.ic_profile_32)
                .into(avatarImageView)

            userNameTextView.text = item.nickname
            agoTextView.text = PrettyTime(Locale.KOREAN).format(dateFormat.parse(item.createdAt))
            if (item.createdAt != item.updatedAt) {
                agoTextView.append(" (수정됨)")
            }

            contentTextView.text = item.content

            recyclerView.isVisible = item.urls.isNotEmpty()
            recyclerView.adapter = CommentImageAdapter(item.urls)

            reportButton.isVisible = myUserId != item.userId
            overflowButton.isVisible = myUserId == item.userId

            reportButton.setOnClickListener {
                onReportButtonClickListener?.invoke(item)
            }

            overflowButton.setOnClickListener {
                val binding = PopupCommentBinding.inflate(LayoutInflater.from(root.context))

                val popup = PopupWindow(binding.root, WRAP_CONTENT, WRAP_CONTENT).apply {
                    isTouchable = true
                    isOutsideTouchable = true
                }

                binding.editButton.setOnClickListener {
                    popup.dismiss()
                    onEditButtonClickListener?.invoke(item)
                }

                binding.removeButton.setOnClickListener {
                    popup.dismiss()
                    onRemoveButtonClickListener?.invoke(item)
                }

                val density = root.context.resources.displayMetrics.density

                popup.showAsDropDown(
                    it,
                    (-(density * (32 + 77))).toInt(),
                    (-(density * (49 + 8 + 8)) / 2).toInt(),
                    Gravity.RIGHT or Gravity.TOP
                )
            }
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
