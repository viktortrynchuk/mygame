package com.example.mygame.engine_and_helpers
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.example.mygame.R
import com.example.mygame.views.OptionUi

class FullScreenOptionDialog : DialogFragment() {

    private lateinit var requestKey: String
    private lateinit var options: ArrayList<OptionUi>
    private var excludeRightPx: Int = 0 // kept for backwards compat (not strictly needed)
    private var rightPanel: View? = null
    private var backgroundResId: Int = 0
    private val panelLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        updateWindowWidth()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestKey = requireArguments().getString(ARG_KEY)!!
        excludeRightPx = requireArguments().getInt(ARG_EXCLUDE_RIGHT, 0)
        backgroundResId = requireArguments().getInt(ARG_BG_RES, 0)
        options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelableArrayList(ARG_OPTIONS, OptionUi::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelableArrayList<OptionUi>(ARG_OPTIONS)!!
        }
        setStyle(STYLE_NO_FRAME, R.style.Dialog_FullScreen_Transparent)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = Dialog(requireContext(), theme)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(true)
        d.setCanceledOnTouchOutside(false)
        return d
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // left-anchored “sheet”
            setGravity(Gravity.START)
            setBackgroundDrawableResource(android.R.color.transparent)
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//            setDimAmount(0.4f)
        }

        // Find the right panel in the host Activity and start listening for size changes
        rightPanel = requireActivity().findViewById(R.id.rightPanel)
        rightPanel?.viewTreeObserver?.addOnGlobalLayoutListener(panelLayoutListener)

        // Initial sizing now
        updateWindowWidth()
    }

    override fun onStop() {
        super.onStop()
        rightPanel?.viewTreeObserver?.removeOnGlobalLayoutListener(panelLayoutListener)
        rightPanel = null
    }

    /** Recompute dialog width = screenWidth - rightPanelWidth (>= 0) */
    private fun updateWindowWidth() {
        val win = dialog?.window ?: return

        val totalW = (requireActivity().window.decorView.width)
            .takeIf { it > 0 } ?: resources.displayMetrics.widthPixels

        val panelW = rightPanel?.width ?: 0
        val targetW = (totalW - panelW).coerceAtLeast(0)

        val lp = win.attributes
        lp.width = targetW
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.START or Gravity.TOP
        win.attributes = lp

        // Safety: some devices still respect this; harmless to call in addition.
        win.setLayout(targetW, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        val root = inflater.inflate(R.layout.dialog_role_fullscreen, container, false)

        // set background image if provided
        root.findViewById<ImageView>(R.id.roleDialogBg).apply {
            if (backgroundResId != 0) setImageResource(backgroundResId)
        }

        val list = root.findViewById<ListView>(R.id.roleList)
        list.adapter = ArrayAdapter(
            requireContext(),
            R.layout.role_list_item,
            R.id.roleItemText,
            options.map { it.title }
        )
        list.setOnItemClickListener { _, _, which, _ ->
            val chosen = options[which]
            parentFragmentManager.setFragmentResult(
                requestKey,
                bundleOf("cancelled" to (chosen.id == -1L), "chosenId" to chosen.id, "position" to which)
            )
            dismissAllowingStateLoss()
        }
        return root
    }

    companion object {
        private const val ARG_OPTIONS = "options"
        private const val ARG_KEY = "key"
        private const val ARG_EXCLUDE_RIGHT = "excludeRightPx"
        private const val ARG_BG_RES = "bgResId"

        fun newInstance(options: ArrayList<OptionUi>, requestKey: String, excludeRightPx: Int, bgResId: Int) =
            FullScreenOptionDialog().apply {
                arguments = bundleOf(
                    ARG_OPTIONS to options,
                    ARG_KEY to requestKey,
                    ARG_EXCLUDE_RIGHT to excludeRightPx,
                    ARG_BG_RES to bgResId
                )
            }
    }
}