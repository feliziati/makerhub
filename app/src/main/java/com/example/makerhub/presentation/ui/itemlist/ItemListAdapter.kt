package com.example.makerhub.presentation.ui.itemlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.makerhub.common.DateUtils.formatMillisToDatetime
import com.example.makerhub.databinding.ItemListEntryBinding
import com.example.makerhub.domain.model.Item

class ItemListAdapter(
    private val onItemClick: ((Item) -> Unit)? = null
) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

    var items = listOf<Item>()
    fun submitList(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemListEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position], onItemClick)
    }

    class ItemViewHolder(private val binding: ItemListEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item, onItemClick: ((Item) -> Unit)?) {
            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }

            binding.textItemId.text = "#${item.id}"
            binding.textItemName.text = item.name
            binding.textItemCategory.text = "Category: ${item.category}"
            binding.textItemStatus.text = item.status.name.replace("_", " ")

            if (item.updatedAt > 0) {
                binding.textItemUpdated.text = "Last Updated: ${item.updatedAt.formatMillisToDatetime()}"
            } else {
                binding.textItemUpdated.text = ""
            }

            com.example.makerhub.common.UiUtils.applyStatusStyle(binding.textItemStatus, item.status)
        }
    }
}
