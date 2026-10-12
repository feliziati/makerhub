package com.example.makerhub.presentation.ui.itemdetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.makerhub.common.IResult
import com.example.makerhub.common.ItemStatus
import com.example.makerhub.common.UiUtils
import com.example.makerhub.databinding.ActivityItemDetailBinding
import com.example.makerhub.di.component.ComponentHolder
import com.example.makerhub.domain.model.Borrower
import com.example.makerhub.domain.model.Item
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItemDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
    }

    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var checkoutHistoryAdapter: CheckoutHistoryAdapter
    private var itemId: Int = -1

    private val viewModel: ItemDetailViewModel by viewModels {
        ItemDetailViewModel.Factory(
            itemId,
            ComponentHolder.getComponent(this)
        )
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        itemId = intent.extras?.getInt(EXTRA_ITEM_ID) ?: run {
            this.finish()
            return
        }

        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainItemDetail) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        checkoutHistoryAdapter = CheckoutHistoryAdapter()
        binding.rvCheckoutHistory.layoutManager = LinearLayoutManager(this)
        binding.rvCheckoutHistory.adapter = checkoutHistoryAdapter
        binding.rvCheckoutHistory.isNestedScrollingEnabled = false
    }

    private fun setupButtons(item: Item) {
        binding.btnMarkLost.setOnClickListener {
            showMarkLostConfirmationDialog()
        }

        binding.btnCheckout.setOnClickListener {
            showBorrowerSelectionDialog()
        }

        binding.btnReturn.setOnClickListener {
            viewModel.returnItem()
        }
    }

    private fun showBorrowerSelectionDialog() {
        val borrowers = viewModel.borrowersLiveData.value
        if (borrowers.isNullOrEmpty()) {
            Toast.makeText(this, "No borrowers available", Toast.LENGTH_SHORT).show()
            return
        }

        val borrowerNames = borrowers.map { "${it.name} (${it.email})" }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Borrower")
            .setItems(borrowerNames) { _, which ->
                val selectedBorrower = borrowers[which]
                viewModel.checkoutItem(selectedBorrower)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMarkLostConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Mark as Lost")
            .setMessage("Are you sure you want to mark this item as lost?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.markAsLost()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.itemLiveData.observe(this) { item ->
            if (item != null) {
                setupViews(item)
            }
        }

        viewModel.borrowersLiveData.observe(this) {
            // Keep borrowers LiveData active
        }

        viewModel.checkoutsLiveData.observe(this) { checkouts ->
            if (checkouts.isNullOrEmpty()) {
                binding.textEmptyHistory.visibility = View.VISIBLE
                binding.rvCheckoutHistory.visibility = View.GONE
            } else {
                binding.textEmptyHistory.visibility = View.GONE
                binding.rvCheckoutHistory.visibility = View.VISIBLE
                checkoutHistoryAdapter.submitList(checkouts)
            }
        }

        viewModel.actionResult.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is IResult.Success -> {
                        Toast.makeText(this, result.data, Toast.LENGTH_SHORT).show()
                    }

                    is IResult.Error -> {
                        val msg = result.errorMessage ?: result.thr.message ?: "Action failed"
                        showErrorDialog(msg)
                    }
                }
                viewModel.clearActionResult()
            }
        }
    }

    private fun setupViews(item: Item) {
        setupButtons(item)

        binding.textItemId.text = "Item ID: #${item.id}"
        binding.textItemName.text = item.name
        binding.textItemCategory.text = "Category: ${item.category}"
        binding.textItemStatus.text = item.status.toString()

        if (item.updatedAt > 0) {
            binding.textItemUpdated.text =
                "Last Updated: ${dateFormat.format(Date(item.updatedAt))}"
        } else {
            binding.textItemUpdated.text = ""
        }

        UiUtils.applyStatusStyle(binding.textItemStatus, item.status)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("Unable to complete action with reason: $message")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }
}
