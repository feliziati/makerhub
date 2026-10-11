package com.example.makerhub.presentation.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.makerhub.R
import com.example.makerhub.common.IResult
import com.example.makerhub.data.local.AppDatabase
import com.example.makerhub.data.repository.impl.DataSourceRepositoryImpl
import com.example.makerhub.data.repository.impl.LocalRepositoryImpl
import com.example.makerhub.databinding.ActivityMainBinding
import com.example.makerhub.domain.usecase.ImportDataUseCase
import com.google.gson.Gson
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val TAG = MainActivity::class.java.simpleName

    var report: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
    }

    private fun setupViews() {
        binding.initImportButton.setOnClickListener {
            lifecycleScope.launch {
                when (val result = ImportDataUseCase(DataSourceRepositoryImpl(this@MainActivity, Gson()),
                    LocalRepositoryImpl(
                        AppDatabase.getInstance(this@MainActivity).itemDao(),
                        AppDatabase.getInstance(this@MainActivity).borrowerDao(),
                        AppDatabase.getInstance(this@MainActivity).checkoutDao()
                    )
                ).invoke()) {
                    is IResult.Error -> {
                        Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    is IResult.Success -> {
                        Toast.makeText(this@MainActivity, "Import success. Report logged", Toast.LENGTH_SHORT).show()
                        binding.viewReportButton.isEnabled = true
                        report = result.data
                    }
                }
            }
        }

        binding.initDeltaButton.setOnClickListener {
            lifecycleScope.launch {
                when (val result = ImportDataUseCase(DataSourceRepositoryImpl(this@MainActivity, Gson()),
                    LocalRepositoryImpl(
                        AppDatabase.getInstance(this@MainActivity).itemDao(),
                        AppDatabase.getInstance(this@MainActivity).borrowerDao(),
                        AppDatabase.getInstance(this@MainActivity).checkoutDao()
                    ),
                    isInit = false
                ).invoke()) {
                    is IResult.Error -> Toast.makeText(this@MainActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                    is IResult.Success -> {
                        Toast.makeText(this@MainActivity, "Import success. Report logged", Toast.LENGTH_SHORT).show()
                        binding.viewReportButton.isEnabled = true
                        report = result.data
                    }
                }
            }
        }
        binding.viewReportButton.setOnClickListener {
            showReport(report)
        }
    }

    private fun showReport(reportLog: String) {
        AlertDialog.Builder(this).apply {
            setMessage(reportLog)
            setPositiveButton("OK") { dialog, id ->
                dialog.dismiss()
                binding.viewReportButton.isEnabled = false
            }
        }.create().show()
    }

}