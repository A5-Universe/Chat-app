package com.a5universe.chatapp.ui.connect.connectFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.a5universe.chatapp.databinding.FragmentHomeConnectBinding
import com.a5universe.chatapp.model.authentication.User
import com.a5universe.chatapp.ui.connect.connectAdapter.FindFriendsAdapter
import com.a5universe.chatapp.ui.connect.viewpagerAdapter.ViewPagerConnectAdapter
import com.google.android.material.tabs.TabLayout


class HomeConnectFragment : Fragment(), FindFriendsAdapter.OnAddFriendClickListener {
    private lateinit var binding: FragmentHomeConnectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeConnectBinding.inflate(layoutInflater,container,false)

        val tabLayout: TabLayout = binding.tab
        val viewPager: ViewPager = binding.viewPager

        val adapter = ViewPagerConnectAdapter(childFragmentManager)
        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)
        return binding.root
    }
    override fun onAddFriendClick(user: User) {
        // Handle the click event here
        Toast.makeText(requireContext(), "Friend added: ${user.name}", Toast.LENGTH_SHORT).show()
    }
}