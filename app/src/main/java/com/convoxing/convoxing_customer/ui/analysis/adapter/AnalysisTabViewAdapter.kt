package com.convoxing.convoxing_customer.ui.analysis.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.convoxing.convoxing_customer.ui.analysis.fragments.AnalysisGrammarFragment
import com.convoxing.convoxing_customer.ui.analysis.fragments.AnalysisVocabFragment


class AnalysisTabViewAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var tabCount: Int,
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    private val tabTitles = arrayOf("Grammar", "Vocabulary")

    fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) AnalysisGrammarFragment()
        else AnalysisVocabFragment()
    }


    override fun getItemCount(): Int {
        return tabCount
    }
}