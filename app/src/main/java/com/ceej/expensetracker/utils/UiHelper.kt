package com.ceej.expensetracker.utils

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import com.google.android.material.textfield.TextInputLayout

object UiHelper {

    fun setErrorState(textInputLayout: TextInputLayout, errorMessage: String, color: Int, errorIcon: Drawable?){
        textInputLayout.apply {
            error = errorMessage
            setErrorIconTintList(ColorStateList.valueOf(color))
            errorIconDrawable = errorIcon
            setHelperTextColor(ColorStateList.valueOf(color))
            boxStrokeErrorColor = ColorStateList.valueOf(color)
            requestFocus()
        }

    }

    fun setValidState(textInputLayout: TextInputLayout, validMessage: String, color: Int, checkIcon: Drawable?){
        textInputLayout.apply {
            error = validMessage
            setErrorIconTintList(ColorStateList.valueOf(color))
            errorIconDrawable = checkIcon
            setErrorTextColor(ColorStateList.valueOf(color))
            boxStrokeErrorColor = ColorStateList.valueOf(color)
        }

    }

}