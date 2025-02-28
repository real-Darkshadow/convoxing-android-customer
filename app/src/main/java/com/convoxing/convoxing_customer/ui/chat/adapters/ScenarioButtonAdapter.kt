package com.convoxing.convoxing_customer.ui.chat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.convoxing.convoxing_customer.R


class ScenarioButtonAdapter(
    private val buttonList: ArrayList<String>,
    private val clickListener: (String) -> Unit
) :
    RecyclerView.Adapter<ScenarioButtonAdapter.ButtonViewHolder>() {

    class ButtonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: TextView = view.findViewById(R.id.itemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_button_rv, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        val buttonModel = buttonList[position]
        holder.button.text = buttonModel
        holder.button.setOnClickListener {
            clickListener(buttonModel)
        }
    }

    override fun getItemCount(): Int = buttonList.size
}