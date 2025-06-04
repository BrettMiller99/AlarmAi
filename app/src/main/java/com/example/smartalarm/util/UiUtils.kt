package com.example.smartalarm.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

/**
 * Utility class for common UI operations.
 */
object UiUtils {
    
    //region Context Extensions
    
    /**
     * Shows a short toast message.
     */
    fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }
    
    /**
     * Shows a short toast message with a string resource.
     */
    fun Context.showToast(@StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        showToast(getString(messageRes), duration)
    }
    
    /**
     * Gets a color from resources.
     */
    fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }
    
    /**
     * Gets a drawable from resources.
     */
    fun Context.getDrawableCompat(@DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(this, drawableRes)
    }
    
    //endregion
    
    //region View Extensions
    
    /**
     * Shows a snackbar with the given message.
     */
    fun View.showSnackbar(
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        actionText: String? = null,
        action: (() -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(this, message, duration)
        if (actionText != null && action != null) {
            snackbar.setAction(actionText) { action() }
        }
        snackbar.show()
    }
    
    /**
     * Shows a snackbar with a string resource.
     */
    fun View.showSnackbar(
        @StringRes messageRes: Int,
        duration: Int = Snackbar.LENGTH_SHORT,
        @StringRes actionTextRes: Int? = null,
        action: (() -> Unit)? = null
    ) {
        val actionText = actionTextRes?.let { context.getString(it) }
        showSnackbar(context.getString(messageRes), duration, actionText, action)
    }
    
    /**
     * Shows the soft keyboard for a view.
     */
    fun View.showKeyboard() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
    
    /**
     * Hides the soft keyboard.
     */
    fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    
    /**
     * Sets the view's visibility to VISIBLE.
     */
    fun View.visible() {
        visibility = View.VISIBLE
    }
    
    /**
     * Sets the view's visibility to GONE.
     */
    fun View.gone() {
        visibility = View.GONE
    }
    
    /**
     * Sets the view's visibility to INVISIBLE.
     */
    fun View.invisible() {
        visibility = View.INVISIBLE
    }
    
    //endregion
    
    //region Fragment Extensions
    
    /**
     * Hides the keyboard if it's showing.
     */
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard() }
    }
    
    /**
     * Shows a toast message.
     */
    fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        context?.showToast(message, duration)
    }
    
    /**
     * Shows a toast message with a string resource.
     */
    fun Fragment.showToast(@StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        context?.showToast(messageRes, duration)
    }
    
    //endregion
    
    //region Activity Extensions
    
    /**
     * Hides the keyboard.
     */
    fun android.app.Activity.hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    
    //endregion
    
    //region Dimension Conversions
    
    /**
     * Converts dp to pixels.
     */
    fun Context.dpToPx(dp: Float): Int {
        val density = resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }
    
    /**
     * Converts pixels to dp.
     */
    fun Context.pxToDp(px: Int): Float {
        val density = resources.displayMetrics.density
        return px / density
    }
    
    /**
     * Converts sp to pixels.
     */
    fun Context.spToPx(sp: Float): Float {
        val scaledDensity = resources.displayMetrics.scaledDensity
        return sp * scaledDensity
    }
    
    //endregion
}
