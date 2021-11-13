package `in`.windrunner.permex

import `in`.windrunner.permex.databinding.LayoutBottomExplanationBinding
import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog

internal class BottomSheetExplanationDialog(
    private val onUserDecision: (Boolean) -> Unit,
    context: Context
) : BottomSheetDialog(context) {

    private lateinit var viewBindings: LayoutBottomExplanationBinding

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBindings = LayoutBottomExplanationBinding.inflate(layoutInflater)
        setContentView(viewBindings.root)

        applyDialogModel()
    }

    private fun applyDialogModel() {
        viewBindings.buttonAllow.setOnClickListener {
            onUserDecision(true)
            dismiss()
        }

        viewBindings.buttonDisallow.setOnClickListener {
            onUserDecision(false)
            dismiss()
        }
    }
}