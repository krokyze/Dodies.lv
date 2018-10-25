package com.krokyze.dodies

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.krokyze.dodies.view.favorites.FavoritesFragment
import com.krokyze.dodies.view.map.MapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = PagerAdapter(supportFragmentManager)
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_map -> view_pager.setCurrentItem(0, true)
                R.id.action_favorites -> view_pager.setCurrentItem(1, true)
            }
            true
        }
    }

    inner class PagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        override fun getCount() = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> MapFragment()
                1 -> FavoritesFragment()
                else -> throw IllegalStateException("there should be only 2 fragments")
            }
        }
    }
}
