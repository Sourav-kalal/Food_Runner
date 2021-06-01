package com.example.food_order_app.fragment

import CustomExpandableListAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import com.example.food_order_app.R


class FAQs : Fragment() {

    private var expandableListView: ExpandableListView? = null
    private var adapter: ExpandableListAdapter? = null
    private var titleList: List<String>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_faqs, container, false)

        expandableListView = view.findViewById(R.id.ev_faqs)
        if (expandableListView != null) {


            val expandableListDetail =
                HashMap<String, List<String>>()
            val about_food_runner:MutableList<String> =
                ArrayList()
            about_food_runner.add("Food Runner App is the mobile application with a sole objective of empowering small businesses, local food outlets and other businesses with a tool that enables them to reach customers within a few taps on the screen. Developed by Sourav Kalal, this mobile application helps you combat the terror of e-commerce giants that are becoming more & more gigantic. With the help of this app, the restaurant owners can market their menu online and also manage everything, from buying process to tracking payments- on their smartphones/devices.")
            val suitable_for_business: MutableList<String> =
                ArrayList()
            suitable_for_business.add("Absolutely! This app is for all those restaurants that have a compelling menu, but are lagging behind simply because they have no online presence.")
            val order_history :  MutableList<String> =
                ArrayList()
            order_history.add("If you are register user of Food Runner you can maintain order history of yours. You just have to login to your user account and click on Order History to view full order details and history.")
            val about_order_placing : MutableList<String> =ArrayList()
            about_order_placing.add("NO, you need to create a account to place order")
            val payment:MutableList<String> =
                ArrayList()
            payment.add("All extra charges are taken by restaurant owner. Food Runner is not taking any extra charges for online payment or online orders.")

            expandableListDetail["What exactly is Food Runner ?"] = about_food_runner
            expandableListDetail["Is this app suitable for my business?"] = suitable_for_business
            expandableListDetail["How can customer can view history of placed order from Food Runner?"] = order_history
            expandableListDetail["Can user place order if user is not registered on Food Runner?"] = about_order_placing
            expandableListDetail["Do I need to pay extra charges for online payment or online orders?"] = payment

            val listData = expandableListDetail
            val context = activity as Context
            titleList = ArrayList(listData.keys)
            adapter = CustomExpandableListAdapter(context, titleList as ArrayList<String>, listData)
            expandableListView!!.setAdapter(adapter)
        }

        return view
    }

}