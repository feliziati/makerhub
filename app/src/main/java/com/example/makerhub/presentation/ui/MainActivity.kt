package com.example.makerhub.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.makerhub.R
import com.example.makerhub.common.IResult
import com.example.makerhub.databinding.ActivityMainBinding
import com.example.makerhub.di.component.ComponentHolder
import com.example.makerhub.presentation.ui.itemlist.ItemListActivity
import com.example.makerhub.presentation.ui.report.ReportActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val TAG = MainActivity::class.java.simpleName

    val component = ComponentHolder.getComponent(this)

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
                when (val result = component.getImportDataUseCase().invoke(true)) {
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
                when (val result = component.getImportDataUseCase().invoke(false)) {
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
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        binding.viewItemsButton.setOnClickListener {
            val intent = Intent(this, ItemListActivity::class.java)
            startActivity(intent)
        }
    }
}
