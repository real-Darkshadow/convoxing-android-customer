package com.convoxing.convoxing_customer.ui.analysis.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.data.remote.models.GrammarAnalysis
import com.convoxing.convoxing_customer.data.remote.models.Replacements
import com.convoxing.convoxing_customer.data.remote.models.VocabAnalysis
import com.convoxing.convoxing_customer.data.remote.models.WordReplacements
import com.convoxing.convoxing_customer.databinding.ItemAnalysisCorrectionsRvBinding
import com.convoxing.convoxing_customer.utils.ExtensionFunctions.gone

class VocabAdapter(
    private val context: Context,
    private var analysis: ArrayList<Replacements>,
) : RecyclerView.Adapter<VocabAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemAnalysisCorrectionsRvBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Replacements) {
            binding.apply {
                mainText.text = data.word
                replacementText.text = data.replacement
                expandIcon.gone()
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
        holder.bind(analysis[position])
    }

    /**
     * Updates the messages and refreshes the adapter.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addAnalysis(newAnalysis: ArrayList<Replacements>) {
        analysis.clear()
        analysis.addAll(newAnalysis)
        notifyDataSetChanged()
    }
}