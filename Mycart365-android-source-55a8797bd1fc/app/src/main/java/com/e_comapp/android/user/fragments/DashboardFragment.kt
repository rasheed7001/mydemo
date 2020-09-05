package com.e_comapp.android.user.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.user.activities.MainPageActivity
import com.e_comapp.android.user.activities.MyCartActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : BaseFragment() {
    var bottomNavigation: BottomNavigationView? = null
    var navigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setLeftMenuIcon(R.drawable.ic_left_menu)
        setTitleWithAddress("Home", "Address")
    }

    override fun menuClicks() {
        super.menuClicks()
        (activity as MainPageActivity).drawer?.openDrawer(GravityCompat.START)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setupDefaults()
        setupEvents()
    }

    private fun init(view: View) {
        bottomNavigation = view.findViewById(R.id.navigation)
        bottomNavigation?.itemIconTintList = null
        navigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    openFragment(HomeFragment(), getString(R.string.str_home))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_search -> {
                    openFragment(SearchFragment(), getString(R.string.str_search))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_my_cart -> {
                    //                                openFragment(new MyCartFragment(),getString(R.string.str_my_cart));
                    val intent = Intent(context, MyCartActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_community -> {
                    openFragment(CommunityFragment(), getString(R.string.str_community))
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setupDefaults() {
        bottomNavigation!!.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        bottomNavigation!!.selectedItemId = R.id.nav_home
    }

    private fun setupEvents() {}
    fun openFragment(fragment: Fragment?, tag: String?) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame, fragment!!, tag)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): DashboardFragment {
            val fragment = DashboardFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}