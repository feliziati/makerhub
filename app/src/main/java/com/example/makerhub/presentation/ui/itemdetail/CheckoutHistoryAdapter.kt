package com.example.makerhub.presentation.ui.itemdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.makerhub.common.DateUtils.formatMillisToDatetime
import com.example.makerhub.common.UiUtils
import com.example.makerhub.databinding.ItemCheckoutEntryBinding
import com.example.makerhub.domain.model.Checkout

class CheckoutHistoryAdapter : RecyclerView.Adapter<CheckoutHistoryAdapter.CheckoutViewHolder>() {

    var checkouts = listOf<Checkout>()

    fun submitList(newCheckouts: List<Checkout>) {
        checkouts = newCheckouts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = checkouts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val binding = ItemCheckoutEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(checkouts[position])
    }

    class CheckoutViewHolder(private val binding: ItemCheckoutEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(checkout: Checkout) {
            binding.textCheckoutBorrower.text = "${checkout.borrower.name} (${checkout.borrower.email})"
            UiUtils.applyStatusStyle(binding.textCheckoutStatus, checkout.status)

            val checkedOutStr = checkout.checkedOutAt.formatMillisToDatetime()
            val dueStr = checkout.dueAt.formatMillisToDatetime()
            binding.textCheckoutDates.text = "Checked Out: $checkedOutStr | Due: $dueStr"

            if (checkout.returnedAt != null && checkout.returnedAt > 0) {
                binding.textCheckoutReturned.visibility = View.VISIBLE
                binding.textCheckoutReturned.text = "Returned: ${checkout.returnedAt.formatMillisToDatetime()}"
            } else {
                binding.textCheckoutReturned.visibility = View.GONE
            }
        }
    }
}
