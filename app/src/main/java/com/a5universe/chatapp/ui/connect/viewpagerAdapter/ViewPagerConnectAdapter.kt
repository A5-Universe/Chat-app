package com.a5universe.chatapp.ui.connect.viewpagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.a5universe.chatapp.ui.connect.connectFragment.FindFriendsFragment
import com.a5universe.chatapp.ui.connect.connectFragment.RequestFriendsFragment
import com.a5universe.chatapp.ui.connect.connectFragment.YourFriendsFragment

class ViewPagerConnectAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
                0 -> FindFriendsFragment()
                1 -> RequestFriendsFragment()
            else -> YourFriendsFragment()
        }
    }

    override fun getCount(): Int {
        return 3 // Number of tabs
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Find Friends"
            1 -> "Request"
            else -> "Your Friends"
        }
    }
}