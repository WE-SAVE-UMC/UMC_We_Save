package com.example.we_save.ui.main.pages.accident

import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.we_save.R
import com.example.we_save.common.Constants.dateFormat
import com.example.we_save.common.extensions.hideKeyboard
import com.example.we_save.common.extensions.setAppAnimation
import com.example.we_save.data.model.PostDto
import com.example.we_save.databinding.FragmentNearMeBinding
import com.example.we_save.databinding.ItemAccidentBinding
import com.example.we_save.databinding.ItemNoticeBinding
import com.example.we_save.ui.MainViewModel
import com.example.we_save.ui.accident.AccidentDetailsFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.util.Locale

const val REQUEST_KEY_REFRESH = "REQUEST_KEY_REFRESH"
const val REQUEST_KEY_UPDATE = "REQUEST_KEY_UPDATE"
const val DATA_KEY_POST_RESULT = "DATA_KEY_POST_RESULT"

class NearMeFragment : Fragment() {
    private var _binding: FragmentNearMeBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<NearMeViewModel>()

    private val postAdapter by lazy { PostAdapter() }
    private val noticeAdapter by lazy { NoticeAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNearMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().supportFragmentManager.setFragmentResultListener(
            REQUEST_KEY_REFRESH,
            viewLifecycleOwner
        ) { _, data ->
            lifecycleScope.launch {
                viewModel.refresh()
            }
        }

        requireActivity().supportFragmentManager.setFragmentResultListener(
            REQUEST_KEY_UPDATE,
            viewLifecycleOwner
        ) { _, data ->
            val result = data.get(DATA_KEY_POST_RESULT)

            if (result is Long) {
                viewModel.recentPosts.value?.let {
                    viewModel.recentPosts.value = it.filter { it.postId != result }
                }

                viewModel.posts.value?.let {
                    viewModel.posts.value = it.filter { it.postId != result }
                }
            } else if (result is PostDto) {
                viewModel.recentPosts.value?.let {
                    val posts = ArrayList(it)
                    val index = posts.indexOfFirst { it.postId == result.postId }
                    if (index >= 0) {
                        posts[index] = result.copy(
                            distance = posts[index].distance,
                            completed = posts[index].completed,
                        )

                        viewModel.recentPosts.value = posts
                    }
                }

                viewModel.posts.value?.let {
                    val posts = ArrayList(it)
                    val index = posts.indexOfFirst { it.postId == result.postId }
                    if (index >= 0) {
                        posts[index] = result.copy(
                            distance = posts[index].distance,
                        )

                        viewModel.posts.value = posts
                    }
                }
            }
        }

        with(binding) {
            viewPager.adapter = noticeAdapter

            searchEditText.addTextChangedListener {
                viewModel.query.value = it?.toString() ?: ""
            }

            searchEditText.setOnEditorActionListener(OnEditorActionListener { v, keyCode, event ->
                if (event == null) return@OnEditorActionListener false

                if ((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard(v)
                    return@OnEditorActionListener true
                }

                false
            })

            currentLocationButton.setOnClickListener {
                if (currentLocationButton.colorFilter != null) return@setOnClickListener

                currentLocationButton.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red_10
                    )
                )

                activityViewModel.updateAddress { _, _ ->
                    currentLocationButton.colorFilter = null
                }
            }

            situationEndFilter.setOnClickListener {
                viewModel.excludeSituationEnd.value = !viewModel.excludeSituationEnd.value
            }

            listOf(
                distanceFilterButton,
                recentFilterButton,
                checkFilterButton
            ).forEachIndexed { index, button ->
                button.setOnClickListener {
                    viewModel.filter.value = NearMeViewModel.Filter.entries[index]
                }
            }

            refreshLayout.setOnRefreshListener {
                lifecycleScope.launch {
                    viewModel.refresh()
                    refreshLayout.isRefreshing = false
                }
            }

            recyclerView.adapter = postAdapter.apply {
                onItemClickListener = {
                    val fragmentManager = requireActivity().supportFragmentManager
                    if (fragmentManager.findFragmentByTag("accident_details") == null) {
                        fragmentManager.commit {
                            setAppAnimation()
                            replace(
                                R.id.fragment_container,
                                AccidentDetailsFragment.getInstance(it.postId),
                                "accident_details"
                            )
                            addToBackStack(null)
                        }
                    }
                }
            }

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                    if (layoutManager.findLastVisibleItemPosition() == postAdapter.itemCount - 1) {
                        lifecycleScope.launch {
                            viewModel.loadMore()
                        }
                    }
                }
            })
        }

        lifecycleScope.launch {
            activityViewModel.address.collectLatest {
                if (it == null) return@collectLatest

                viewModel.address.value = it
                binding.currentAddressTextView.text = it.short
            }
        }

        lifecycleScope.launch {
            viewModel.excludeSituationEnd.collectLatest {
                val typeface = resources.getFont(R.font.pretandard)

                with(binding.situationEndFilter) {
                    isSelected = it
                    (this[0] as TextView).apply {
                        text = if (it) "상황 종료 포함" else "상황 종료 제외"
                        setTypeface(typeface, if (it) Typeface.NORMAL else Typeface.BOLD)
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filter.collectLatest {
                with(binding) {
                    val typeface = resources.getFont(R.font.pretandard)

                    when (it) {
                        NearMeViewModel.Filter.DISTANCE -> {
                            distanceFilterButton.apply {
                                isSelected = true
                                (this[0] as TextView).setTypeface(typeface, Typeface.BOLD)
                            }
                            recentFilterButton.apply {
                                isSelected = false
                                (this[0] as TextView).setTypeface(typeface, Typeface.NORMAL)
                            }
                            checkFilterButton.apply {
                                isSelected = false
                                (this[0] as TextView).setTypeface(typeface, Typeface.NORMAL)
                            }
                        }

                        NearMeViewModel.Filter.RECENT -> {
                            distanceFilterButton.apply {
                                isSelected = false
                                (this[0] as TextView).setTypeface(typeface, Typeface.NORMAL)
                            }
                            recentFilterButton.apply {
                                isSelected = true
                                (this[0] as TextView).setTypeface(typeface, Typeface.BOLD)
                            }
                            checkFilterButton.apply {
                                isSelected = false
                                (this[0] as TextView).setTypeface(typeface, Typeface.NORMAL)
                            }
                        }

                        NearMeViewModel.Filter.TOP -> {
                            distanceFilterButton.apply {
                                isSelected = false
                                (this[0] as TextView).setTypeface(typeface, Typeface.NORMAL)
                            }
                            recentFilterButton.apply {
                                isSelected = false
                                (this[0] as TextView).setTypeface(typeface, Typeface.NORMAL)
                            }
                            checkFilterButton.apply {
                                isSelected = true
                                (this[0] as TextView).setTypeface(typeface, Typeface.BOLD)
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.ticker.collectLatest {
                val nextPosition = binding.viewPager.currentItem + 1
                binding.viewPager.setCurrentItem(
                    if (nextPosition > (noticeAdapter.itemCount - 1)) 0 else nextPosition,
                    false
                )
            }
        }

        lifecycleScope.launch {
            viewModel.recentPosts.collectLatest {
                if (it == null) return@collectLatest

                noticeAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.posts.collectLatest {
                if (it == null) return@collectLatest

                postAdapter.submitList(it)

                binding.refreshLayout.isRefreshing = false
            }
        }
    }

    private class NoticeAdapter : ListAdapter<PostDto, NoticeAdapter.NoticeItemViewHolder>(
        diffUtil
    ) {
        companion object {
            val diffUtil = object : DiffUtil.ItemCallback<PostDto>() {
                override fun areItemsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
                    return oldItem.postId == newItem.postId
                }

                override fun areContentsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
                    return oldItem.postRegionName == newItem.postRegionName && oldItem.title == newItem.title
                }

                override fun getChangePayload(oldItem: PostDto, newItem: PostDto): Any {
                    return Any()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemNoticeBinding.inflate(inflater, parent, false)
            return NoticeItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: NoticeItemViewHolder, position: Int) {
            val item = getItem(position)

            with(holder.binding) {
                accidentAddressTextView.text = item.postRegionName.replace(Regex("[0-9- ]"), "")
                accidentTitleTextView.text = item.title
            }
        }

        class NoticeItemViewHolder(val binding: ItemNoticeBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private class PostAdapter : ListAdapter<PostDto, PostAdapter.AccidentItemViewHolder>(
        diffUtil
    ) {
        companion object {
            val diffUtil = object : DiffUtil.ItemCallback<PostDto>() {
                override fun areItemsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
                    return oldItem.postId == newItem.postId
                }

                override fun areContentsTheSame(oldItem: PostDto, newItem: PostDto): Boolean {
                    return oldItem == newItem
                }
            }
        }

        var onItemClickListener: ((PostDto) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccidentItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAccidentBinding.inflate(inflater, parent, false)
            return AccidentItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: AccidentItemViewHolder, position: Int) {
            val item = getItem(position)

            with(holder.binding) {
                root.get(0).setOnClickListener {
                    onItemClickListener?.invoke(item)
                }

                typeTextView.text = item.category
                titleTextView.text = item.title
                agoTextView.text =
                    PrettyTime(Locale.KOREAN).format(dateFormat.parse(item.createdAt))
                descriptionTextView.text = item.content
                checkCountTextView.text = item.hearts.toString()
                falsehoodCountTextView.text = item.dislikes.toString()
                imageCountTextView.text = item.imageCount.toString()

                distanceTextView.isVisible = item.distance?.isNotBlank() == true
                distanceTextView.text = item.distance
                addressTextView.text = item.postRegionName

                situationEndIndicator.isInvisible = !item.completed

                val url = item.url
                (imageView.parent.parent as View).isVisible = url != null
                if (url != null) {
                    Glide.with(imageView)
                        .load(url)
                        .into(imageView)
                }
            }
        }

        class AccidentItemViewHolder(val binding: ItemAccidentBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}
