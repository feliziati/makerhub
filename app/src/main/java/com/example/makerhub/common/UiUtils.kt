package com.example.makerhub.common

import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.makerhub.R

object UiUtils {

    @JvmStatic
    fun applyStatusStyle(textView: TextView, status: ItemStatus?) {
        if (status == null) {
            return
        }

        val text = status.name.replace("_", " ")
        val (bgColorRes, textColorRes) = when (status) {
            ItemStatus.AVAILABLE -> Pair(R.color.status_available_bg, R.color.status_available_text)
            ItemStatus.ON_LOAN -> Pair(R.color.status_on_loan_bg, R.color.status_on_loan_text)
            ItemStatus.LOST -> Pair(R.color.status_lost_bg, R.color.status_lost_text)
            ItemStatus.RETIRED -> Pair(R.color.status_retired_bg, R.color.status_retired_text)
        }

        applyStyleInternal(textView, text, bgColorRes, textColorRes)
    }

    @JvmStatic
    fun applyStatusStyle(textView: TextView, status: CheckoutStatus?) {
        if (status == null) {
            return
        }

        val text = status.name.replace("_", " ")
        val (bgColorRes, textColorRes) = when (status) {
            CheckoutStatus.ACTIVE -> Pair(R.color.status_active_bg, R.color.status_active_text)
            CheckoutStatus.RETURNED -> Pair(R.color.status_returned_bg, R.color.status_returned_text)
            CheckoutStatus.LOST -> Pair(R.color.status_lost_bg, R.color.status_lost_text)
        }

        applyStyleInternal(textView, text, bgColorRes, textColorRes)
    }

    private fun applyStyleInternal(
        textView: TextView,
        text: String,
        bgColorRes: Int,
        textColorRes: Int
    ) {
        val context = textView.context
        val bgColor = ContextCompat.getColor(context, bgColorRes)
        val textColor = ContextCompat.getColor(context, textColorRes)

        textView.text = text
        textView.setTextColor(textColor)

        val paddingLeft = textView.paddingLeft
        val paddingTop = textView.paddingTop
        val paddingRight = textView.paddingRight
        val paddingBottom = textView.paddingBottom

        val density = context.resources.displayMetrics.density
        val cornerRadiusPx = 8f * density

        try {
            val backgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = cornerRadiusPx
                setColor(bgColor)
            }
            textView.background = backgroundDrawable
            textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        } catch (_: Exception) {
            // Ignored in unmocked JVM test environments
        }
    }
}
