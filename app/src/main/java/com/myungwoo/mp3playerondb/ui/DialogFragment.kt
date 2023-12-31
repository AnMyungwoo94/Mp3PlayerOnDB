package com.myungwoo.mp3playerondb.ui

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.myungwoo.mp3playerondb.databinding.FragmentDialogBinding

class DialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDialogCancel.setOnClickListener {
            dismiss()
        }
        binding.btnRemittanceConfirm.setOnClickListener {
            dismiss()
            startActivity(Intent(requireContext(), RecordActivity::class.java))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            (this as BottomSheetDialog).behavior.peekHeight =
                Resources.getSystem().displayMetrics.heightPixels
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}