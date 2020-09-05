package com.e_comapp.android.seller.activities

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.seller.adapters.DrawerCustomAdapter
import com.e_comapp.android.seller.fragments.*
import com.e_comapp.android.seller.models.LeftMenuModel
import com.e_comapp.android.util.component.CircleImageView
import com.e_comapp.android.utils.AlertUtils
import java.util.*

class MainPageActivity : BaseActivity(), ViewMenuSelectedListener {
    var adapter: DrawerCustomAdapter? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerList: ListView? = null
    var mDrawerToggle: ActionBarDrawerToggle? = null
    var username: TextView? = null
    var email: TextView? = null
    var userImage: CircleImageView? = null
    var mCurrentMenu = 0
    var headerRootLayout: LinearLayout? = null
    var isFirstTime = true
    private val isNotificationCame = false
    private val isCameFromNewIntent = false
    private val isAppBroughtToForeground = false
    private val currentActivity: String? = null
    var userId: String? = null
    var needToLogoutBlockedUser = false
    private var isSeller = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupEvents() {
        headerRootLayout!!.setOnClickListener { selectMenu(7, true, null) }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    private fun setupDefaults() {
        username!!.text = "Hello, " + app.appPreference!!.getFirstName()
        val drawerItem: ArrayList<LeftMenuModel?> = ArrayList<LeftMenuModel?>()
        drawerItem.add(LeftMenuModel(R.drawable.ic_my_account, getString(R.string.str_my_account)))
        drawerItem.add(LeftMenuModel(R.drawable.home_grey, getString(R.string.str_stock_management)))
        drawerItem.add(LeftMenuModel(R.drawable.ic_history_grey, getString(R.string.str_order_history)))
        drawerItem.add(LeftMenuModel(R.drawable.ic_notification_grey, getString(R.string.str_notification)))
        drawerItem.add(LeftMenuModel(R.drawable.faqs_grey, getString(R.string.str_faqs)))
        drawerItem.add(LeftMenuModel(R.drawable.bracket, getString(R.string.str_custom_support)))
        drawerItem.add(LeftMenuModel(R.drawable.logout, getString(R.string.str_logout)))
        adapter = DrawerCustomAdapter(this, R.layout.nav_item, drawerItem)
        mDrawerList!!.adapter = adapter
        val defaultFrag: Fragment
        defaultFrag = HomeFragment()
        val t = supportFragmentManager.beginTransaction()
        t.replace(R.id.content_frame, defaultFrag)
        t.commit()
    }

    private fun init() {
        headerRootLayout = findViewById<View>(R.id.header_root) as LinearLayout
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        mDrawerList = findViewById<View>(R.id.navList) as ListView
        val inflater = layoutInflater
        userImage = findViewById<View>(R.id.iv_photo) as CircleImageView
        username = findViewById<View>(R.id.tv_name) as TextView
        mDrawerList!!.onItemClickListener = DrawerItemClickListener()
        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)
        setupDrawerToggle()
        isSeller = intent.hasExtra("from_seller")
    }

    override fun menuClicked() {
        super.menuClicked()
        mDrawerLayout!!.openDrawer(Gravity.LEFT)
    }

    override fun onMenuSelected(menu: Int) {
        selectMenu(menu, false, null)
    }

    var fragment: BaseFragment? = null
    fun selectMenu(menu: Int, isToggle: Boolean, args: Bundle?) {
        fragment = null
        when (menu) {
            0 -> fragment = MyAccountFragment()
            1 -> fragment = StockManagementFragment()
            2 -> fragment = OrderHistoryFragment()
            3 -> fragment = NotificationFragment()
            4 -> fragment = FAQsFragment()
            5 -> fragment = CustomerSupportFragment()
            6 -> showLogoutAlert()
            else -> fragment = HomeFragment()
        }
        if (fragment != null) {
            fragment!!.arguments = args
            addFragment(fragment, menu, isToggle)
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
            if (menu != 7) {
                adapter!!.updateSelectedMenu(menu)
            }
        }
    }

    private fun addFragment(fragment: BaseFragment?, menu: Int, isToggle: Boolean) {
        if (fragment != null && menu != mCurrentMenu) {
            mCurrentMenu = menu
            val t = supportFragmentManager.beginTransaction()
            t.replace(R.id.content_frame, fragment)
            t.commit()
        }
    }

    fun getDrawer(): DrawerLayout? {
        return mDrawerLayout
    }

    private inner class DrawerItemClickListener : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            selectMenu(position, true, null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {}
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle!!.syncState()
    }

    fun setupDrawerToggle() {
        mDrawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name)
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle!!.syncState()
    }

    protected fun isActivityRunning(activityClass: Class<*>): Boolean {
        val activityManager = baseContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(Int.MAX_VALUE)
        for (task in tasks) {
            if (activityClass.canonicalName.equals(task.baseActivity.className, ignoreCase = true)) return true
        }
        return false
    }

    fun showLogoutAlert() {
        AlertUtils.showAlert(this, getString(R.string.app_name), getString(R.string.str_are_you_sure_want_to_logout), DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
    }

    companion object {
        private const val TAG = "TestCase.csss"
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}