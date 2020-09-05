package com.e_comapp.android.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.e_comapp.android.EComApp
import com.e_comapp.android.R
import com.e_comapp.android.di.MY_CART_SCOPE_ID
import com.e_comapp.android.di.MY_CART_SCOPE_QUALIFIER
import com.e_comapp.android.seller.activities.MainPageActivity
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.scope.Scope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class BaseFragment(private val parentContext: CoroutineContext = EmptyCoroutineContext) : Fragment(), KoinComponent {

    @JvmField
    var dialog: ProgressDialog? = null
    val myActivity: BaseActivity?
        get() = activity as BaseActivity?

    val mainActivity: MainPageActivity?
        get() = activity as MainPageActivity?

    val app: EComApp
        get() = requireActivity().applicationContext as EComApp

    protected lateinit var coroutineScope: CoroutineScope
        private set

    val viewModelScope: Scope? get() = _currentScope

    private var _currentScope = internalCreateScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        coroutineScope =
                CoroutineScope(Dispatchers.Main + parentContext + SupervisorJob(parentContext[Job]))
    }


    open fun menuClicks() {}
    fun rightClicks() {}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = ProgressDialog(context)
        dialog!!.setMessage("Loading...")
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.setCancelable(false)
    }

    fun setTitle(text: String?) {
        val lnrTitle = view?.findViewById<LinearLayout>(R.id.lnrTitle)
        lnrTitle?.visibility = View.GONE
        val txtTitle = view?.findViewById<TextView>(R.id.txt_title)
        txtTitle?.visibility = View.VISIBLE
        txtTitle?.text = text
    }

    fun setTitleWithAddress(title: String?, address: String?) {
        val txtTitle = view?.findViewById<TextView>(R.id.txt_title)
        txtTitle?.visibility = View.GONE
        val lnrTitle = view?.findViewById<LinearLayout>(R.id.lnrTitle)
        lnrTitle?.visibility = View.VISIBLE
        val txtTitle2 = view?.findViewById<TextView>(R.id.txt_title2)
        val txtAddress = view?.findViewById<TextView>(R.id.txt_address)
        txtTitle2?.text = title
        txtAddress?.text = address
    }

    fun setRightMenuIcon(res: Int) {
        val imgRight = view?.findViewById<ImageView>(R.id.img_right_menu)
        imgRight?.setImageResource(res)
        imgRight?.visibility = View.VISIBLE
        imgRight?.setOnClickListener { rightClicks() }
    }

    fun setLeftMenuIcon(res: Int) {
        val imgMenu = view?.findViewById<ImageView>(R.id.img_menu)
        imgMenu?.setImageResource(res)
        imgMenu?.visibility = View.VISIBLE
        imgMenu?.setOnClickListener { menuClicks() }
    }


    /**
     * Destroy/close out a logged in scope. Should only be called when a user is logging out
     */
    fun destroyScope() {
        _currentScope.also { scope ->
            _currentScope = null
            scope?.close()
        }
    }

    /**
     * Start a new logged in scope.  If one is already active then it is returned without creation
     */
    fun startcope(): Scope? = internalCreateScope().apply {
        _currentScope = this
    }

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
}