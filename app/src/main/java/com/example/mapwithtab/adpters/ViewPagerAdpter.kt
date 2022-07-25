package com.example.mapwithtab.adpters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mapwithtab.ui.CountryListFragment
import com.example.mapwithtab.ui.MapsFragment
import com.example.mapwithtab.ui.SavedLocationsFragment

private const val NUM_TABS = 3

class ViewPagerAdpter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS;
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return MapsFragment()
            1 -> return SavedLocationsFragment()
        }
        return CountryListFragment()
    }
}