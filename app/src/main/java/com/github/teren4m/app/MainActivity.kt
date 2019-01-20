package com.github.teren4m.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tab1 = LayoutInflater.from(this).inflate(R.layout.tab, null)
        val tab2 = LayoutInflater.from(this).inflate(R.layout.tab, null)
        val tab3 = LayoutInflater.from(this).inflate(R.layout.tab, null)

        val adapter = MyFragmentPagerAdapter(
            arrayOf(
                MyFragmentPagerAdapter.Item(
                    MyFragment(),
                    tab1
                ),
                MyFragmentPagerAdapter.Item(
                    MyFragment(),
                    tab2
                ),
                MyFragmentPagerAdapter.Item(
                    MyFragment(),
                    tab3
                )
            ),
            supportFragmentManager
        )

        pager.offscreenPageLimit = 1
        pager.adapter = adapter

        tab.setupWithViewPager(pager)
    }

}
