package com.convoxing.convoxing_customer.ui.analysis.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.data.remote.models.GrammarAnalysis
import com.convoxing.convoxing_customer.databinding.ItemAnalysisCorrectionsRvBinding

class GrammarAdapter(
    private val context: Context,
    private var analysis: ArrayList<GrammarAnalysis>,
) : RecyclerView.Adapter<GrammarAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemAnalysisCorrectionsRvBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: GrammarAnalysis, position: Int) {
            binding.apply {
                mainText.text = data.wrongPortion
                replacementText.text = data.rightPortion
                explainationTv.text = data.errorExplanation
                root.setOnClickListener {
                    if (explainationTv.visibility == View.GONE) {
                        explainationTv.visibility = View.VISIBLE
                    } else {
                        explainationTv.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = ItemAnalysisCorrectionsRvBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = analysis.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(analysis[position], position)
    }

    /**
     * Updates the messages and refreshes the adapter.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addAnalysis(newAnalysis: ArrayList<GrammarAnalysis>) {
        analysis.clear()
        analysis.addAll(newAnalysis)
        notifyDataSetChanged()
    }
}