package com.github.teren4m.app

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.teren4m.tab.CustomViewFragmentPagerAdapter

class MyFragmentPagerAdapter(
    private val fragments: Array<Item>,
    fragmentManager: FragmentManager
) : CustomViewFragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment =
        fragments[position].fragment

    override fun getCount(): Int = fragments.size

    override fun getView(position: Int): View =
        fragments[position].view

    class Item(val fragment: Fragment, val view: View)
}