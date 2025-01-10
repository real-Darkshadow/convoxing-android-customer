package com.convoxing.convoxing_customer.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.R
import com.convoxing.convoxing_customer.data.remote.models.VocabularyWord

class CardStackAdapter(private val words: List<VocabularyWord>) :
    RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordTextView: TextView = view.findViewById(R.id.tv_word)
        val definitionTextView: TextView = view.findViewById(R.id.tv_definition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vocab_card_stack, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val word = words[position]
        holder.wordTextView.text = word.word
        holder.definitionTextView.text = word.definition
    }

    override fun getItemCount(): Int = words.size
}