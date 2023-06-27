@file:Suppress("DEPRECATION")

package cn.zsmarter.hulk.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Create By YP On 2022/5/12.
 * 用途：ViewPager适配器
 */
class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    private val fragments: List<Fragment>
): FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }
}