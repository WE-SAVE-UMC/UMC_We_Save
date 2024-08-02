package com.example.we_save.ui.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.we_save.R
import com.example.we_save.databinding.BottomSheetSelectBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetSelectFragment: BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetSelectBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 커스텀 레이아웃을 인플레이트
        return inflater.inflate(R.layout.bottom_sheet_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 레이아웃의 뷰에 대한 참조를 가져와서 이벤트를 처리합니다.


    }

    override fun getTheme(): Int {
        // 커스텀 스타일을 적용합니다.
        return R.style.TransparentBottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        // BottomSheet의 배경을 완전히 투명하게 설정
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 터치 이벤트 전달 설정
//        dialog?.window?.decorView?.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
//                    // 이벤트를 아래로 전달하지 않음
//                    false
//                }
//                MotionEvent.ACTION_UP -> {
//                    // 이벤트를 아래로 전달함
//                    false
//                }
//                else -> false
//            }
//        }
    }
}