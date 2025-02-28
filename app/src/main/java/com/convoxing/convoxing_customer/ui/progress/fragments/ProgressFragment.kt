package com.convoxing.convoxing_customer.ui.progress.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.remote.models.SessionHistory
import com.convoxing.convoxing_customer.databinding.FragmentProgressBinding
import com.convoxing.convoxing_customer.ui.progress.viewmodel.ProgressViewModel
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.changeStatusBarColor
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.navigateSafe
import com.convoxing.convoxing_customer.utils.Status
import com.convoxing.convoxing_customer.utils.Utils.formatIsoDate
import com.convoxing.convoxing_customer.utils.Utils.formatIsoDateToReadableDate
import com.convoxing.convoxing_customer.utils.ui.RoundedBarChartRenderer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProgressFragment : Fragment() {
    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProgressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor(R.color.alice_blue, 1)
        viewModel.getUserById()
        viewModel.getVocabularyAvgWords()
        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.userDetailResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response?.data?.user?.let { user ->
                        binding.apply {
                            starsText.text = user.totalStars.toString()
                            gemsText.text = user.totalGems.toString()
                            streakText.text = user.sessionStreak.toString()
                            totalSession.text = "${user.totalSessions} SESSIONS"
                        }

                    }
                }

                Status.ERROR -> {}
                Status.LOADING -> {

                }

                Status.IDLE -> {}
            }
        }
        viewModel.avgVocabWordsResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Status.SUCCESS -> {
                    response?.data?.sessions?.let { sessions ->
                        binding.apply {
                            val parsedData = sessions.mapNotNull { session ->
                                val timestamp =
                                    session.sessionTimestamp?.formatIsoDateToReadableDate()
                                val words = session.averageTotalWords
                                if (timestamp != null && words != null) Pair(
                                    timestamp,
                                    words
                                ) else null
                            }
                            val groupedData = groupDataByWeek(parsedData)
                            setupBarChart(binding.barChart, groupedData)
                        }

                    }
                }

                Status.ERROR -> {}
                Status.LOADING -> {}
                Status.IDLE -> {}
            }
        }
    }

    private fun setListeners() {
        binding.settingsBtn.setOnClickListener {
            findNavController().navigateSafe(R.id.settingsFragment)
        }
        binding.featureRequestCard.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://convoxing.com/featureRequest")
                )
            )
        }
    }

    private fun groupDataByWeek(dataList: List<Pair<String, Int>>): Map<String, Int> {
        val groupedData = mutableMapOf<String, Int>()
        val calendar = Calendar.getInstance()
        val weekDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val parserFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        dataList.forEach { (sessionDateStr, averageTotalWords) ->
            val sessionDate = parserFormat.parse(sessionDateStr) ?: return@forEach
            calendar.time = sessionDate

            // Set to start of the week (Monday)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val startOfWeek = weekDateFormat.format(calendar.time)

            // Add 6 days to get to Sunday
            calendar.add(Calendar.DATE, 6)
            val endOfWeek = weekDateFormat.format(calendar.time)

            val weekRange = "$startOfWeek - $endOfWeek"
            groupedData[weekRange] = groupedData.getOrDefault(weekRange, 0) + averageTotalWords
        }

        return groupedData.toSortedMap()
    }

    private fun setupBarChart(barChart: BarChart, groupedData: Map<String, Int>) {
        val entries = mutableListOf<BarEntry>()
        val labels = groupedData.keys.toList()

        groupedData.values.forEachIndexed { index, value ->
            entries.add(BarEntry(index.toFloat(), value.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.color = Color.parseColor("#ffeaae")
        barDataSet.valueTextSize = 12f

        val barData = BarData(barDataSet)

        barData.barWidth = 0.4f
        barChart.data = barData
        barChart.setFitBars(true)
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return labels.getOrNull(value.toInt()) ?: ""
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f// Ensures proper spacing
        xAxis.setCenterAxisLabels(false) // Prevents extra gaps
        xAxis.spaceMin = 0.1f // Minimize spacing on left
        xAxis.spaceMax = 0.1f // Minimize spacing on right

        // ✅ Ensure bars start properly
        barChart.xAxis.axisMinimum = -0.5f
        barChart.xAxis.axisMaximum = entries.size - 0.1f
        barChart.xAxis.axisLineColor = Color.TRANSPARENT
        barChart.xAxis.setDrawAxisLine(false)
        // ✅ Y-Axis settings
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        // ✅ Chart styling
//        barChart.renderer =
//            RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.isHighlightPerTapEnabled = false
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}