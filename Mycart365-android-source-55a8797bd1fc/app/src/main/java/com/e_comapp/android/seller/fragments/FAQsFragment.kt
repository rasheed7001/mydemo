package com.e_comapp.android.seller.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.seller.activities.MainPageActivity
import com.e_comapp.android.seller.adapters.FAQsAdapter
import com.e_comapp.android.seller.models.FAQ
import java.util.*

class FAQsFragment : BaseFragment() {
    var adapter: FAQsAdapter? = null
    var recyclerView: RecyclerView? = null
    var faqList = ArrayList<FAQ>()

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setLeftMenuIcon(R.drawable.ic_left_menu)
        setTitle(getString(R.string.str_faqs))
    }

    override fun menuClicks() {
        super.menuClicks()
        (requireActivity() as MainPageActivity).getDrawer()?.openDrawer(GravityCompat.START)
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
        return inflater.inflate(R.layout.fragment_faqs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        setupDefaults()
    }

    private fun init(view: View) {
        recyclerView = view.findViewById(R.id.rv_faqs)
        faqList.add(FAQ("What is My Account?", "Lorem ipsum,or lipsum as it is sometimes known, is dummy text used in layout out print, graphic web designs.", false))
        faqList.add(FAQ("What is My Account?", "Lorem ipsum,or lipsum as it is sometimes known, is dummy text used in layout out print, graphic web designs.", false))
        faqList.add(FAQ("What is My Account?", "Lorem ipsum,or lipsum as it is sometimes known, is dummy text used in layout out print, graphic web designs.", false))
        faqList.add(FAQ("What is My Account?", "Lorem ipsum,or lipsum as it is sometimes known, is dummy text used in layout out print, graphic web designs.", false))
        faqList.add(FAQ("What is My Account?", "Lorem ipsum,or lipsum as it is sometimes known, is dummy text used in layout out print, graphic web designs.", false))
        adapter = FAQsAdapter(context, faqList)
        recyclerView?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView?.adapter = adapter
    }

    private fun setupDefaults() {}

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri?) {}
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
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
         * @return A new instance of fragment FAQsFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): FAQsFragment {
            val fragment = FAQsFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}