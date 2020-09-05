package com.e_comapp.android.views

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.e_comapp.android.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.Exception

/**
 * RoundedBottomSheetDialogFragment
 */
abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    /**
     * Abstract function to implement action listener
     * */
    abstract fun setActionListener()

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?):
            Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionListener()
    }

}