package com.e_comapp.android.user.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.databinding.FragmentHomeUserBinding
import com.e_comapp.android.seller.models.Industry
import com.e_comapp.android.user.activities.PickupAndDropActivity
import com.e_comapp.android.user.activities.SellerListActivity
import com.e_comapp.android.user.adapters.HomeBannerAdapter
import com.e_comapp.android.user.adapters.IndustryListAdapter
import com.e_comapp.android.user.adapters.IndustryListAdapter.OnIndustryListClickedListener
import com.e_comapp.android.utils.ParamConstant
import com.e_comapp.android.viewmodel.HomeViewModel
import com.e_comapp.android.views.CustomBtn
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.viewmodel.scope.getViewModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : BaseFragment(), OnIndustryListClickedListener {
    private var clGrocery: ConstraintLayout? = null
    private var imgPickupAndDrop: ImageView? = null
    private var imgCourier: ImageView? = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    var listAdapter: IndustryListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentHomeUserBinding

    private lateinit var pagerAdapter: HomeBannerAdapter

    private val viewModel by lazy {
        viewModelScope?.getViewModel<HomeViewModel>(this)
    }


    private val adapter = IndustryListAdapter(this)

    var rlIndustryList: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_home_user,
                container,
                false
        )

        pagerAdapter = HomeBannerAdapter(requireContext())

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.cateList.adapter = adapter
        binding.ivPhoto.adapter = pagerAdapter
//        binding.tabs.setupWithViewPager(binding.ivPhoto)
        TabLayoutMediator(binding.tabs, binding.ivPhoto) { tab, position ->
//            tab.text = tabTitles[position]
            binding.ivPhoto.setCurrentItem(tab.position, true)
        }.attach()

        enableAutoSwitch()
        return binding.root
    }

    private fun enableAutoSwitch() {
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                binding.ivPhoto.post(Runnable {
                    if (pagerAdapter.itemCount - 1 == binding.ivPhoto.currentItem ) {
                        binding.ivPhoto.currentItem = 0
                    } else {
                        binding.ivPhoto.currentItem = (binding.ivPhoto.currentItem + 1)
                    }
                })
            }
        }
        val timer = Timer()
        timer.schedule(timerTask, 3000, 3000)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setupEvents()

        viewModel?.appDefault?.observe(viewLifecycleOwner, Observer {
            adapter.setData(it.industry ?: emptyList())
        })
    }

    private fun init(view: View) {
        clGrocery = view.findViewById(R.id.cl_stock_management)
        imgPickupAndDrop = view.findViewById(R.id.imgPickupDrop)
        imgCourier = view.findViewById(R.id.imgCourier)
    }

    private fun setupEvents() {
        imgPickupAndDrop!!.setOnClickListener {
            val intent = Intent(context, PickupAndDropActivity::class.java)
            startActivity(intent)
        }
        imgCourier!!.setOnClickListener { showSignupDialog() }
    }

    private fun showSignupDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_outstation_delivery)
        dialog.setCancelable(false)
        val btnAgree: CustomBtn = dialog.findViewById(R.id.btnApply)
        btnAgree.setOnClickListener {
            val intent = Intent(context, PickupAndDropActivity::class.java)
            startActivity(intent)
        }
        dialog.show()
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeUserFragment.
         */
        fun newInstance(param1: String?, param2: String?): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onIndustryClicked(industry: Industry?) {
        val intent = Intent(context, SellerListActivity::class.java)
        intent.putExtras(Bundle().apply {
            putString(ParamConstant.INDUSTRY_ID, industry?.id)
            putString(ParamConstant.INDUSTRY_NAME, industry?.industry)
        })
        startActivity(intent)
    }
}