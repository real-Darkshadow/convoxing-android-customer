package com.convoxing.convoxing_customer.ui.practice.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.convoxing.convoxing_customer.ui.practice.fragments.RolePlayFragment
import com.convoxing.convoxing_customer.ui.practice.fragments.ThemesFragment

class PracticeFragPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    private val tabTitles = arrayOf("Themes", "Role-Play")

    fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ThemesFragment()
            1 -> RolePlayFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }


}