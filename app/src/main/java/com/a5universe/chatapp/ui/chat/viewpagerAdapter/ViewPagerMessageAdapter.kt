package com.a5universe.chatapp.ui.chat.viewpagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.a5universe.chatapp.ui.chat.chatFragment.ChatFragment
import com.a5universe.chatapp.ui.group.groupFragment.GroupsFragment

class ViewPagerMessageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ChatFragment()
            else -> GroupsFragment()
//            else -> StatusFragment()
        }
    }

    override fun getCount(): Int {
        return 2 // Number of tabs
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Chats"
            else -> "Groups"
//            else -> "Status"
        }
    }

//    test
//    problem is tablayout tile color is ugly
//    test
}