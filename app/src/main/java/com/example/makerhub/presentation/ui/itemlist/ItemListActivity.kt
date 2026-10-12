package com.example.makerhub.presentation.ui.itemlist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.makerhub.R
import com.example.makerhub.common.ItemStatus
import com.example.makerhub.databinding.ActivityItemListBinding
import com.example.makerhub.di.component.ComponentHolder
import com.example.makerhub.domain.model.Item
import com.example.makerhub.presentation.ui.itemdetail.ItemDetailActivity
import com.example.makerhub.presentation.ui.report.ReportActivity
import kotlinx.coroutines.launch

class ItemListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemListBinding
    private lateinit var adapter: ItemListAdapter

    private val viewModel: ItemListViewModel by viewModels {
        ItemListViewModel.Factory(ComponentHolder.getComponent(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityItemListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainItemList) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupRecyclerView()
        setupChips()
        observeData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ItemListAdapter { item ->
            val intent = Intent(this, ItemDetailActivity::class.java).apply {
                putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id)
            }
            startActivity(intent)
        }
        binding.itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.itemsRecyclerView.adapter = adapter
    }

    private fun setupChips() {
        binding.statusChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedStatus = when (checkedIds.firstOrNull()) {
                R.id.chip_available -> ItemStatus.AVAILABLE
                R.id.chip_on_loan -> ItemStatus.ON_LOAN
                R.id.chip_lost -> ItemStatus.LOST
                R.id.chip_retired -> ItemStatus.RETIRED
                else -> null
            }
            viewModel.setStatusFilter(selectedStatus)
        }
    }

    private fun observeData() {
        viewModel.allItems.observe(this) { items ->
            updateCounts(items ?: emptyList())
        }

        viewModel.filteredItems.observe(this) { filteredList ->
            val totalSize = viewModel.allItems.value?.size ?: 0
            val currentFilter = viewModel.selectedStatusFilter.value
            val filterText =
                currentFilter?.name?.replace("_", " ") ?: getString(R.string.filter_all)

            binding.textFilterSummary.text = getString(
                R.string.filter_summary,
                filteredList.size,
                totalSize,
                filterText
            )

            adapter.submitList(filteredList)

            if (filteredList.isNullOrEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.itemsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.itemsRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun updateCounts(items: List<Item>) {
        val availableCount = items.count { it.status == ItemStatus.AVAILABLE }
        val onLoanCount = items.count { it.status == ItemStatus.ON_LOAN }
        val lostCount = items.count { it.status == ItemStatus.LOST }
        val retiredCount = items.count { it.status == ItemStatus.RETIRED }
        val totalCount = items.size

        binding.chipAll.text = getString(R.string.count_all, totalCount)
        binding.chipAvailable.text = getString(R.string.count_available, availableCount)
        binding.chipOnLoan.text = getString(R.string.count_on_loan, onLoanCount)
        binding.chipLost.text = getString(R.string.count_lost, lostCount)
        binding.chipRetired.text = getString(R.string.count_retired, retiredCount)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.init_import -> {
                lifecycleScope.launch {
                    viewModel.importData(true)
                }
                true
            }

            R.id.delta_import -> {
                lifecycleScope.launch {
                    viewModel.importData(false)
                }
                true
            }

            R.id.report -> {
                val intent = Intent(this, ReportActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
