package com.example.mygame.engine_and_helpers

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.mygame.views.OptionUi

class OptionPickerDialog : DialogFragment() {

    private var checked = -1
    private var clicked = false
    private var okBtn: Button? = null
    private var cancelBtn: Button? = null

    private lateinit var requestKey: String
    private lateinit var options: ArrayList<OptionUi>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestKey = requireArguments().getString(ARG_KEY)!!
        options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelableArrayList(ARG_OPTIONS, OptionUi::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelableArrayList<OptionUi>(ARG_OPTIONS)!!
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val titles = options.map { it.title }.toTypedArray()

        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Choose an option")
            .setSingleChoiceItems(titles, checked) { _, which ->
                checked = which
                okBtn?.isEnabled = true
            }
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", null)
            .create()

        dlg.setOnShowListener {
            okBtn = dlg.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                isEnabled = checked >= 0
                setOnClickListener {
                    if (clicked || checked < 0) return@setOnClickListener
                    clicked = true
                    val chosen = options[checked]
                    parentFragmentManager.setFragmentResult(
                        requestKey,
                        bundleOf(
                            "cancelled" to false,
                            "chosenId" to chosen.id,
                            "position" to checked
                        )
                    )
                    dismissAllowingStateLoss()
                }
            }
            cancelBtn = dlg.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    parentFragmentManager.setFragmentResult(
                        requestKey,
                        bundleOf("cancelled" to true)
                    )
                    dismissAllowingStateLoss()
                }
            }
        }

        dlg.setCanceledOnTouchOutside(false)
        isCancelable = false
        return dlg
    }

    companion object {
        private const val ARG_OPTIONS = "options"
        private const val ARG_KEY = "key"

        fun newInstance(options: ArrayList<OptionUi>, requestKey: String) =
            OptionPickerDialog().apply {
                arguments = bundleOf(
                    ARG_OPTIONS to options,
                    ARG_KEY to requestKey
                )
            }
    }
}