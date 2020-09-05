package com.e_comapp.android.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.e_comapp.android.EComApp
import com.e_comapp.android.R
import com.e_comapp.android.di.MY_CART_SCOPE_ID
import com.e_comapp.android.di.MY_CART_SCOPE_QUALIFIER
import kotlinx.coroutines.*
import org.koin.android.ext.android.getKoin
import org.koin.core.scope.Scope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseActivity(private val parentContext: CoroutineContext = EmptyCoroutineContext) : AppCompatActivity() {
    @JvmField
    var dialog: ProgressDialog? = null
    var currentFragment: BaseFragment? = null

    protected lateinit var coroutineScope: CoroutineScope
        private set

    val viewModelScope: Scope? get() = _currentScope


    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }

    private fun internalCreateScope(): Scope? {
        val created = getKoin().getScopeOrNull(MY_CART_SCOPE_ID) == null
        val scope = getKoin()
                .getOrCreateScope(MY_CART_SCOPE_ID, MY_CART_SCOPE_QUALIFIER)

        if (created) {
            scope.declare(this)
        }

        return scope
    }

    private var _currentScope = internalCreateScope()

    /**
     * Destroy/close out a logged in scope. Should only be called when a user is logging out
     */
    fun destroyLoggedInScope() {
        _currentScope.also { scope ->
            _currentScope = null
            scope?.close()
        }
    }

    /**
     * Start a new logged in scope.  If one is already active then it is returned without creation
     */
    fun startLoggedInScope(): Scope? = internalCreateScope().apply {
        _currentScope = this
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coroutineScope =
                CoroutineScope(Dispatchers.Main + parentContext + SupervisorJob(parentContext[Job]))

        dialog = ProgressDialog(this)
        dialog!!.setMessage("Loading...")
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
    }

    val app: EComApp
        get() = application as EComApp

    var txtNotification: TextView? = null
    var txtHome: TextView? = null
    var txtProfile: TextView? = null
    var txtTitle: TextView? = null
    var txtTitle2: TextView? = null
    var txtSearch: TextView? = null
    var txtMyCart: TextView? = null
    var txtCommunity: TextView? = null
    var txtAddress: TextView? = null
    var txtOrders: TextView? = null
    var imgMenu: ImageView? = null
    var imgRight: ImageView? = null
    var lnrTitle: LinearLayout? = null

    fun setupToolbar() {
        txtTitle = findViewById(R.id.txt_title)
        imgMenu = findViewById(R.id.img_menu)
        imgRight = findViewById(R.id.img_right_menu)
        imgRight?.setOnClickListener(View.OnClickListener { rightMenuClicked() })
        imgMenu?.setOnClickListener(View.OnClickListener { menuClicked() })
    }

    fun setupCustToolbar() {
        txtTitle = findViewById(R.id.txt_title)
        txtTitle2 = findViewById(R.id.txt_title2)
        txtAddress = findViewById(R.id.txt_address)
        lnrTitle = findViewById(R.id.lnrTitle)
        imgRight = findViewById(R.id.img_right_menu)
        imgMenu = findViewById(R.id.img_menu)
        imgRight?.setOnClickListener(View.OnClickListener { rightMenuClicked() })
        imgMenu?.setOnClickListener(View.OnClickListener { menuClicked() })
    }

    fun setAddress(address: String?) {
        lnrTitle!!.visibility = View.VISIBLE
        txtAddress!!.text = address
        txtTitle!!.visibility = View.GONE
    }

    fun setCustTitle(title: String?) {
        lnrTitle!!.visibility = View.GONE
        txtTitle!!.text = title
        txtTitle!!.visibility = View.VISIBLE
        imgRight?.visibility = View.GONE
    }

    fun setCustTitleWithAddress(title: String?) {
        lnrTitle!!.visibility = View.VISIBLE
        txtTitle2!!.text = title
        txtTitle!!.visibility = View.GONE
    }

    fun setupCustMenus() {
        txtHome = findViewById(R.id.txtNavHome)
        txtMyCart = findViewById(R.id.txtMyCart)
        txtSearch = findViewById(R.id.txtSearch)
        txtCommunity = findViewById(R.id.txtCommunity)
        txtOrders = findViewById(R.id.txtOrders)
        txtHome?.setOnClickListener(View.OnClickListener {
            setCustSelected(1)
            homeClicked()
        })
        txtSearch?.setOnClickListener(View.OnClickListener {
            setCustSelected(2)
            searchClicked()
        })
        txtCommunity?.setOnClickListener(View.OnClickListener {
            setCustSelected(4)
            communityClicked()
        })
        txtOrders?.setOnClickListener(View.OnClickListener {
            setCustSelected(4)
            ordersClicked()
        })
        txtMyCart?.setOnClickListener(View.OnClickListener {
            setCustSelected(3)
            myCartClicked()
        })
    }

    fun ordersClicked() {}
    fun searchClicked() {}
    fun communityClicked() {}
    fun myCartClicked() {}
    open fun rightMenuClicked() {
        if (currentFragment != null) {
            currentFragment!!.rightClicks()
        }
    }

    open fun menuClicked() {
        if (currentFragment != null) {
            currentFragment!!.menuClicks()
        }
    }

    fun setMenuIcon(res: Int) {
        imgMenu!!.setImageResource(res)
        imgMenu!!.visibility = View.VISIBLE
    }

    fun setRightMenuIcon(res: Int) {
        imgRight!!.setImageResource(res)
        imgRight!!.visibility = View.VISIBLE
    }

    fun setTitle(title: String?) {
        txtTitle!!.text = title
    }

    fun setupSellerMenu() {
        txtHome = findViewById(R.id.txtNavHome)
        txtNotification = findViewById(R.id.txtNotification)
        txtProfile = findViewById(R.id.txtProfile)
        txtHome?.setOnClickListener(View.OnClickListener {
            setSelected(1)
            homeClicked()
        })
        txtProfile?.setOnClickListener(View.OnClickListener {
            setSelected(3)
            profileClicked()
        })
        txtNotification?.setOnClickListener(View.OnClickListener {
            setSelected(2)
            notificationClicked()
        })
    }

    fun notificationClicked() {}
    fun homeClicked() {}
    fun profileClicked() {}
    fun setCustSelected(pos: Int) {
        custResetAll()
        when (pos) {
            1 -> {
                txtHome!!.setTextColor(ContextCompat.getColor(this@BaseActivity, R.color.colorAccent))
                txtHome!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.seller_home_s, 0, 0)
            }
            2 -> {
                txtSearch!!.setTextColor(ContextCompat.getColor(this@BaseActivity, R.color.colorAccent))
                txtSearch!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.search_cust_s, 0, 0)
            }
            3 -> {
                txtMyCart!!.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                txtMyCart!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.cust_mycart_s, 0, 0)
            }
            4 -> {
                txtCommunity!!.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                txtCommunity!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.cust_community_s, 0, 0)
            }
        }
    }

    private fun custResetAll() {
        txtCommunity!!.setTextColor(ContextCompat.getColor(this, R.color.txt_title))
        txtCommunity!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.community_cust, 0, 0)
        txtHome!!.setTextColor(ContextCompat.getColor(this, R.color.txt_title))
        txtHome!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.seller_home, 0, 0)
        txtMyCart!!.setTextColor(ContextCompat.getColor(this, R.color.txt_title))
        txtMyCart!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.mycart_cust, 0, 0)
        txtSearch!!.setTextColor(ContextCompat.getColor(this, R.color.txt_title))
        txtSearch!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.search_cust, 0, 0)
    }

    fun setSelected(pos: Int) {
        resetAll()
        when (pos) {
            1 -> {
                txtHome!!.setTextColor(ContextCompat.getColor(this@BaseActivity, R.color.colorAccent))
                txtHome!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.seller_home_s, 0, 0)
            }
            2 -> {
                txtNotification!!.setTextColor(ContextCompat.getColor(this@BaseActivity, R.color.colorAccent))
                txtNotification!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.seller_notification_s, 0, 0)
            }
            3 -> {
                txtProfile!!.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                txtProfile!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.seller_profile_s, 0, 0)
            }
        }
    }

    private fun resetAll() {
        txtNotification!!.setTextColor(ContextCompat.getColor(this, R.color.txt_title))
        txtNotification!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.seller_notification, 0, 0)
        txtHome!!.setTextColor(ContextCompat.getColor(this, R.color.txt_title))
        txtHome!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.seller_home, 0, 0)
        txtProfile!!.setTextColor(ContextCompat.getColor(this, R.color.txt_title))
        txtProfile!!.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.seller_profile, 0, 0)
    }
}