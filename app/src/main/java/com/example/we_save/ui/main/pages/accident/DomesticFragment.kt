package com.example.we_save.ui.main.pages.accident

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.we_save.R
import com.example.we_save.common.extensions.hideKeyboard
import com.example.we_save.common.extensions.setAppAnimation
import com.example.we_save.data.model.Accident
import com.example.we_save.databinding.FragmentDomesticBinding
import com.example.we_save.databinding.ItemAccidentBinding
import com.example.we_save.databinding.ItemNoticeBinding
import com.example.we_save.ui.MainViewModel
import com.example.we_save.ui.accident.AccidentDetailsFragment
import com.example.we_save.ui.accident.ParentFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.util.Date


class DomesticFragment : Fragment() {
    private var _binding: FragmentDomesticBinding? = null
    private val binding get() = _binding!!

    private val activityViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<NearMeViewModel>()

    private val accidentAdapter by lazy { AccidentAdapter() }
    private val noticeAdapter by lazy { NoticeAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDomesticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
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

            distanceFilterButton.setOnClickListener {
                viewModel.filter.value = NearMeViewModel.Filter.DISTANCE
            }

            recentFilterButton.setOnClickListener {
                viewModel.filter.value = NearMeViewModel.Filter.RECENT
            }

            checkFilterButton.setOnClickListener {
                viewModel.filter.value = NearMeViewModel.Filter.CHECK
            }

            viewPager.adapter = noticeAdapter

            recyclerView.adapter = accidentAdapter.apply {
                onItemClickListener = {
                    val fragmentManager = requireActivity().supportFragmentManager
                    if (fragmentManager.findFragmentByTag("accident_details") == null) {
                        fragmentManager.commit {
                            setAppAnimation()
                            replace(
                                R.id.fragment_container,
                                AccidentDetailsFragment.getInstance(it, ParentFragment.DOMESTIC),
                                "accident_details"
                            )
                            addToBackStack(null)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            activityViewModel.address.collectLatest {
                if (it == null) return@collectLatest

                val elements = it.getAddressLine(0).split(" ").toMutableList()
                elements.remove(it.countryName) // 국가
                elements.remove(it.adminArea)   // 도 또는 특별시
                elements.remove(it.locality)    // 시
                elements.remove(it.featureName) // 번지

                binding.currentAddressTextView.text = elements.joinToString(" ")
            }
        }

        lifecycleScope.launch {
            viewModel.excludeSituationEnd.collectLatest {
                with(binding.situationEndFilter) {
                    isSelected = it
                    (get(0) as TextView).text = if (it) "상황 종료 포함" else "상황 종료 제외"
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filter.collectLatest {
                with(binding) {
                    when (it) {
                        NearMeViewModel.Filter.DISTANCE -> {
                            distanceFilterButton.isSelected = true
                            recentFilterButton.isSelected = false
                            checkFilterButton.isSelected = false
                        }

                        NearMeViewModel.Filter.RECENT -> {
                            distanceFilterButton.isSelected = false
                            recentFilterButton.isSelected = true
                            checkFilterButton.isSelected = false
                        }

                        NearMeViewModel.Filter.CHECK -> {
                            distanceFilterButton.isSelected = false
                            recentFilterButton.isSelected = false
                            checkFilterButton.isSelected = true
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            flow {
                while (true) {
                    delay(5000)
                    emit(1)
                }
            }.collectLatest {
                val nextPosition = binding.viewPager.currentItem + 1
                binding.viewPager.setCurrentItem(
                    if (nextPosition > (noticeAdapter.itemCount - 1)) 0 else nextPosition,
                    false
                )
            }
        }

        lifecycleScope.launch {
            viewModel.recentAccidents.collectLatest {
                noticeAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.accidents.collectLatest {
                accidentAdapter.submitList(it)
            }
        }
    }

    private class NoticeAdapter : ListAdapter<Accident, NoticeAdapter.NoticeItemViewHolder>(
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemNoticeBinding.inflate(inflater, parent, false)
            return NoticeItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: NoticeItemViewHolder, position: Int) {
            val item = getItem(position)

            with(holder.binding) {
                accidentAddressTextView.text = item.type.title
                accidentTitleTextView.text = item.title
            }
        }

        class NoticeItemViewHolder(val binding: ItemNoticeBinding) :
            RecyclerView.ViewHolder(binding.root)
    }

    private class AccidentAdapter : ListAdapter<Accident, AccidentAdapter.AccidentItemViewHolder>(
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

        var onItemClickListener: ((Accident) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccidentItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemAccidentBinding.inflate(inflater, parent, false)
            binding.distanceTextView.isVisible = false
            return AccidentItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: AccidentItemViewHolder, position: Int) {
            val item = getItem(position)

            with(holder.binding) {
                root.get(0).setOnClickListener {
                    onItemClickListener?.invoke(item)
                }

                typeTextView.text = item.type.title
                titleTextView.text = item.title
                agoTextView.text = PrettyTime().format(Date(item.timestamp))
                descriptionTextView.text = item.description
                checkCountTextView.text = "0"
                falsehoodCountTextView.text = "0"
                imageCountTextView.text = "${item.images.count()}"

                addressTextView.text = item.address
                situationEndIndicator.isInvisible = !item.isEndSituation

                (imageView.parent.parent as View).isVisible = item.images.isNotEmpty()
                if (item.images.isNotEmpty()) {
                    Glide.with(imageView)
                        .load(item.images.first())
                        .into(imageView)
                }
            }
        }

        class AccidentItemViewHolder(val binding: ItemAccidentBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}
