package com.example.makerhub.presentation.ui.report

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.makerhub.R
import com.example.makerhub.databinding.ActivityReportBinding
import com.example.makerhub.di.component.ComponentHolder

class ReportActivity : AppCompatActivity() {

    lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val reportLog = ComponentHolder.getComponent(this).getImportDataUseCase().getReport()
        if (reportLog.isEmpty()) {
            binding.reportLog.text = "No import report available yet. Run an import from the main screen."
        } else {
            binding.reportLog.text = reportLog
        }
    }
}