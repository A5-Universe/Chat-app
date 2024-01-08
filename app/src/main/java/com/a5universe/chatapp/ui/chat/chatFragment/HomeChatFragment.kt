package com.a5universe.chatapp.ui.chat.chatFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.a5universe.chatapp.databinding.FragmentHomeChatBinding
import com.a5universe.chatapp.ui.chat.viewpagerAdapter.ViewPagerMessageAdapter
import com.google.android.material.tabs.TabLayout


class HomeChatFragment : Fragment() {
    private lateinit var binding: FragmentHomeChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeChatBinding.inflate(layoutInflater,container,false)

        val tabLayout: TabLayout = binding.tab
        val viewPager: ViewPager = binding.viewPager

        val adapter = ViewPagerMessageAdapter(childFragmentManager)
        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)
        return binding.root
    }


}