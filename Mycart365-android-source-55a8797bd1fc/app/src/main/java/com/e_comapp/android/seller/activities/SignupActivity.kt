package com.e_comapp.android.seller.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.seller.models.AppDefaultsParser
import com.e_comapp.android.seller.models.City
import com.e_comapp.android.seller.models.State
import com.e_comapp.android.utils.*
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomEditText
import com.e_comapp.android.views.MyAutoCompleteTextView
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class SignupActivity : BaseActivity() {
    val TAG = javaClass.name
    private var cityList: ArrayList<City?>? = ArrayList()
    private var stateList: ArrayList<State?>? = ArrayList()
    private var cityAdapter: ArrayAdapter<City?>? = null
    private var stateAdapter: ArrayAdapter<State?>? = null
    private var errorFlag = false
    var btnCompleteSignup: CustomBtn? = null
    var etStoreName: CustomEditText? = null
    var etServiceIndustry: CustomEditText? = null
    var etEmail: CustomEditText? = null
    var etCompanyRegNo: CustomEditText? = null
    var etFSSAINumber: CustomEditText? = null
    var etBranchCode: CustomEditText? = null
    var etAdminName: CustomEditText? = null
    var etLandLine: CustomEditText? = null
    var etAddress: CustomEditText? = null
    var etPin: CustomEditText? = null
    var etBankAccountName: CustomEditText? = null
    var etEtBankAccountNo: CustomEditText? = null
    var etIFSC: CustomEditText? = null
    var etCity: MyAutoCompleteTextView? = null
    var etState: MyAutoCompleteTextView? = null
    var storeName: String? = null
    var serviceIndustry: String? = null
    var email: String? = null
    var companyRegNo: String? = null
    var fssaiNumber: String? = null
    var branchCode: String? = null
    var adminName: String? = null
    var landline: String? = null
    var city: String? = null
    var state: String? = null
    var pin: String? = null
    var bankAccountName: String? = null
    var bankAccountNumber: String? = null
    var ifsc: String? = null
    var imgPhoto: ImageView? = null

    var addresss: String? = "null"

    private var imagPaths: Array<String?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        init()
        setupDefaults()
        setupEvents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.Companion.REQUEST_PICTURE_FROM_GALLERY) {
                if (data != null) {
                    GetPhotoFromGallery(data).execute()
                }
            } else if (requestCode == REQUEST_PICTURE_FROM_CAMERA) {
                val f = TempManager.getTempPictureFile(this)
                if (f != null) {
                    var path = f.absolutePath
                    val compressImage = CompressImage(this)
                    path = compressImage.compressImage(path)
                    imagPaths = arrayOf(path)
                    val file = File(imagPaths!![0])
                    if (file.exists()) {
                        startCrop(file)
                    }
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                handleCrop(resultCode, result)
            }
        }
    }

    private fun init() {
        imgPhoto = findViewById(R.id.imgPhoto)
        etStoreName = findViewById(R.id.et_first_name)
        etServiceIndustry = findViewById(R.id.et_last_name)
        etEmail = findViewById(R.id.et_email)
        etCompanyRegNo = findViewById(R.id.et_company_reg_no)
        etFSSAINumber = findViewById(R.id.et_fssai_no)
        etBranchCode = findViewById(R.id.et_branch_code)
        etAdminName = findViewById(R.id.et_admin_name)
        etLandLine = findViewById(R.id.et_landline)
        etAddress = findViewById(R.id.et_address)
        etCity = findViewById(R.id.et_city)
        etState = findViewById(R.id.et_state)
        etPin = findViewById(R.id.et_pin)
        etBankAccountName = findViewById(R.id.et_bank_account_name)
        etEtBankAccountNo = findViewById(R.id.et_bank_account_number)
        etIFSC = findViewById(R.id.et_ifsc)
        btnCompleteSignup = findViewById(R.id.btn_signup)
    }

    private fun setupDefaults() {
        etStoreName!!.addTextChangedListener(textWatcher)
        etServiceIndustry!!.addTextChangedListener(textWatcher)
        etEmail!!.addTextChangedListener(textWatcher)
        etCompanyRegNo!!.addTextChangedListener(textWatcher)
        etFSSAINumber!!.addTextChangedListener(textWatcher)
        etBranchCode!!.addTextChangedListener(textWatcher)
        etAdminName!!.addTextChangedListener(textWatcher)
        etLandLine!!.addTextChangedListener(textWatcher)
        etAddress!!.addTextChangedListener(textWatcher)
        etCity!!.addTextChangedListener(textWatcher)
        etState!!.addTextChangedListener(textWatcher)
        etPin!!.addTextChangedListener(textWatcher)
        etBankAccountName!!.addTextChangedListener(textWatcher)
        etEtBankAccountNo!!.addTextChangedListener(textWatcher)
        etIFSC!!.addTextChangedListener(textWatcher)
        val parser = Gson().fromJson(app.appPreference?.getAppDefaults(), AppDefaultsParser::class.java)
        cityList = parser.cities as ArrayList<City?>
        stateList = parser.states as ArrayList<State?>
        cityAdapter = ArrayAdapter(this, R.layout.layout_drop_down_item, cityList)
        stateAdapter = ArrayAdapter(this, R.layout.layout_drop_down_item, stateList)
        etCity!!.setAdapter(cityAdapter)
        etState!!.setAdapter(stateAdapter)
    }

    private fun setupEvents() {
        imgPhoto!!.setOnClickListener {
            DeviceUtils.hideSoftKeyboard(this@SignupActivity)
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                if (checkAndRequestLocationPermissions()) {
                    promptMediaOption()
                }
            } else {
                promptMediaOption()
            }
        }
        etCity!!.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val selected = adapterView.adapter.getItem(i) as City
            etCity!!.setText(selected.city)
        }
        etState!!.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            val selected = adapterView.adapter.getItem(i) as State
            etState!!.setText(selected.name)
        }
        btnCompleteSignup!!.setOnClickListener {
            if (etStoreName!!.text.toString().also { storeName = it }.isEmpty()) {
                etStoreName!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etServiceIndustry!!.text.toString().also { serviceIndustry = it }.isEmpty()) {
                etServiceIndustry!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etEmail!!.text.toString().also { email = it }.isEmpty()) {
                etEmail!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etCompanyRegNo!!.text.toString().also { companyRegNo = it }.isEmpty()) {
                etCompanyRegNo!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etFSSAINumber!!.text.toString().also { fssaiNumber = it }.isEmpty()) {
                etFSSAINumber!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etBranchCode!!.text.toString().also { branchCode = it }.isEmpty()) {
                etBranchCode!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etAdminName!!.text.toString().also { adminName = it }.isEmpty()) {
                etAdminName!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etLandLine!!.text.toString().also { landline = it }.isEmpty()) {
                etLandLine!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etAddress!!.text.toString().also { addresss = it }.isEmpty()) {
                etAddress!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etCity!!.text.toString().also { city = it }.isEmpty()) {
                etCity!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etState!!.text.toString().also { state = it }.isEmpty()) {
                etState!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etPin!!.text.toString().also { pin = it }.isEmpty()) {
                etPin!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etBankAccountName!!.text.toString().also { bankAccountName = it }.isEmpty()) {
                etBankAccountName!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etEtBankAccountNo!!.text.toString().also { bankAccountNumber = it }.isEmpty()) {
                etEtBankAccountNo!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etIFSC!!.text.toString().also { ifsc = it }.isEmpty()) {
                etIFSC!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (!errorFlag) callPostSellerDetails()

//                startActivity(new Intent(SignupActivity.this,MainPageActivity.class));
        }
    }

    fun callPostSellerDetails() {
        DeviceUtils.hideSoftKeyboard(this)
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        val requestBody = HashMap<String, RequestBody>()
        requestBody["storeName"] = createPartFromString(storeName)
        requestBody["adminName"] = createPartFromString(adminName)
        requestBody["email"] = createPartFromString(email)
        requestBody["regNo"] = createPartFromString(companyRegNo)
        requestBody["fssaiNo"] = createPartFromString(fssaiNumber)
        requestBody["branchCode"] = createPartFromString(branchCode)
        requestBody["landline"] = createPartFromString(landline)
        requestBody["pincode"] = createPartFromString(pin)
        requestBody["address"] = createPartFromString(addresss)
        requestBody["city"] = createPartFromString(city)
        requestBody["state"] = createPartFromString(state)
        requestBody["industry"] = createPartFromString(serviceIndustry)
        requestBody["bankName"] = createPartFromString(bankAccountName)
        requestBody["bankAcNo"] = createPartFromString(bankAccountNumber)
        requestBody["ifscCode"] = createPartFromString(ifsc)
        var userPhoto: MultipartBody.Part? = null
        if (imagPaths != null) {
            val file = File(imagPaths!![0])
            val requestFile = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            userPhoto = createFormData("uploadImg", file.name, requestFile)
            requestBody["imgName"] = createPartFromString(file.name)
        }
        app.retrofitInterface.postSellerDetails("S", userPhoto, requestBody).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.dismiss()
                Log.e(TAG, content)
                var error = false
                var message = ""
                try {
                    val jsonObject = JSONObject(content)
                    error = jsonObject.getBoolean("error")
                    message = jsonObject.getString("message")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                if (!error) {
                    AlertUtils.showToast(this@SignupActivity, message)
                    startActivity(Intent(this@SignupActivity, MainPageActivity::class.java))
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.dismiss()
                AlertUtils.showAlert(this@SignupActivity, message)
            }
        })
    }

    private fun checkAndRequestLocationPermissions(): Boolean {
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), AppConstants.Companion.REQUEST_PERMISSION_READ_STORAGE)
            return false
        }
        return true
    }

    private fun handleCrop(resultCode: Int, result: CropImage.ActivityResult) {
        if (resultCode == Activity.RESULT_OK) {
            val uri = result.uri
            imagPaths = arrayOf(uri.path)
            val file = File(imagPaths!![0])
            if (file.exists()) {
                imgPhoto!!.setImageURI(uri)
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(this, result.error.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCrop(file: File) {
        CropImage.activity(Uri.fromFile(file))
                .setActivityTitle("Profile image crop")
                .setAllowFlipping(false)
                .setAllowRotation(false)
                .start(this)
    }

    private fun promptMediaOption() {
        val ITEMS = arrayOf("Take Picture", "Choose Image")
        openOptionDialog(this, ITEMS, "" + getString(R.string.app_name), DialogInterface.OnClickListener { dialog, which ->
            if (which == 0) {
                openCamera()
            } else {
                openGallery()
            }
        })
    }

    private fun openCamera() {
        val filePath = TempManager.createTempPictureFile(this).absolutePath
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(filePath)))
        } else {
            val file = File(filePath)
            val photoUri = FileProvider
                    .getUriForFile(applicationContext, applicationContext.packageName + ".provider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_PICTURE_FROM_CAMERA)
    }

    private fun openGallery() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), AppConstants.Companion.REQUEST_PICTURE_FROM_GALLERY)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), AppConstants.Companion.REQUEST_PICTURE_FROM_GALLERY)
        }
    }

    fun promptSettings(type: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(String.format(resources.getString(R.string.denied_title), type))
        builder.setMessage(String.format(getString(R.string.denied_msg), type))
        builder.setPositiveButton(getString(R.string.go_to_appsettings)) { dialog, which ->
            dialog.dismiss()
            goToSettings()
        }
        builder.setNegativeButton(R.string.str_cancel, null)
        builder.setCancelable(false)
        builder.show()
    }

    fun goToSettings() {
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + this.packageName))
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(myAppSettings)
    }

    private fun showRequestDialog() {
        showDialogOK(resources.getString(R.string.storage_permission_required),
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> checkAndRequestLocationPermissions()
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                    dialog.dismiss()
                })
    }

    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }

    inner class GetPhotoFromGallery(data: Intent) : AsyncTask<Void?, Void?, Void?>() {
        private val data: Intent?
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            var photoPath = FileUtils.createNewTempProfileFile(this@SignupActivity, "profile").path
            val compressImage = CompressImage(this@SignupActivity)
            photoPath = compressImage.compressImage(photoPath)
            imagPaths = arrayOf(photoPath)
            val file = File(imagPaths!![0])
            if (file.exists()) {
                startCrop(file)
            }
        }

        init {
            this.data = data
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val f = FileUtils.createNewTempProfileFile(this@SignupActivity, "profile").absolutePath
            if (data != null) {
                try {
                    val inputStream = contentResolver.openInputStream(data.data)
                    val fileOutputStream = FileOutputStream(f)
                    FileUtils.copyStream(inputStream, fileOutputStream)
                    fileOutputStream.close()
                    inputStream.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }

    }

    private fun createPartFromString(descriptionString: String?): RequestBody {
        var descriptionString = descriptionString
        if (descriptionString.equals("null", ignoreCase = true)) {
            descriptionString = ""
        }
        return RequestBody.create(MULTIPART_FORM_DATA.toMediaTypeOrNull(), descriptionString!!)
    }

    var textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            checkRequiredFields()
        }

        override fun afterTextChanged(editable: Editable) {
            checkRequiredFields()
        }
    }

    private fun checkRequiredFields() {
        btnCompleteSignup!!.isEnabled = (!etStoreName!!.text.toString().isEmpty() && !etServiceIndustry!!.text.toString().isEmpty()
                && !etEmail!!.text.toString().isEmpty() && !etCompanyRegNo!!.text.toString().isEmpty()
                && !etFSSAINumber!!.text.toString().isEmpty() && !etBranchCode!!.text.toString().isEmpty()
                && !etAdminName!!.text.toString().isEmpty() && !etLandLine!!.text.toString().isEmpty()
                && !etAddress!!.text.toString().isEmpty() && !etCity!!.text.toString().isEmpty()
                && !etState!!.text.toString().isEmpty() && !etPin!!.text.toString().isEmpty()
                && !etBankAccountName!!.text.toString().isEmpty() && !etEtBankAccountNo!!.text.toString().isEmpty()
                && !etIFSC!!.text.toString().isEmpty() && !etServiceIndustry!!.text.toString().isEmpty())
    }

    companion object {
        const val REQUEST_PICTURE_FROM_GALLERY = 200
        const val REQUEST_PICTURE_FROM_CAMERA = 201
        private fun openOptionDialog(context: Context, items: Array<String>, title: String, positiveClick: DialogInterface.OnClickListener) {
            val adapter: ListAdapter = object : ArrayAdapter<String?>(
                    context, android.R.layout.select_dialog_item, items) {
                override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view.findViewById<View>(android.R.id.text1) as TextView
                    textView.text = getItem(position)
                    textView.textSize = 16f
                    return view
                }
            }
            val builder = AlertUtils.getBuilder(context)
            builder!!.setTitle(title)
            builder.setAdapter(adapter, positiveClick)
            builder.create().show()
        }

        private const val MULTIPART_FORM_DATA = "multipart/form-data"
    }
}