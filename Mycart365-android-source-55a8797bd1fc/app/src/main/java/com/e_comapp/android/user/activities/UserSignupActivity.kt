package com.e_comapp.android.user.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.models.CustUpdateDetailsParser
import com.e_comapp.android.utils.*
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomEditText
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class UserSignupActivity : BaseActivity() {
    val TAG = javaClass.name
    var etFirstName: CustomEditText? = null
    var etLastName: CustomEditText? = null
    var etEmail: CustomEditText? = null
    var imgPhoto: ImageView? = null
    var btnSignup: CustomBtn? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    private var errorFlag = false
    private var imagPaths: Array<String?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup1)
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
            } else if (requestCode == AppConstants.Companion.REQUEST_PICTURE_FROM_CAMERA) {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.Companion.REQUEST_PERMISSION_READ_STORAGE -> {
                val perms: MutableMap<String, Int> = HashMap()
                perms[Manifest.permission.READ_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
                //                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < permissions.size) {
                        perms[permissions[i]] = grantResults[i]
                        i++
                    }
                    // Check for both permissions
                    if (perms[Manifest.permission.READ_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                                requestPermissions(AppConstants.Companion.PERMISSIONS, 5)
                            }
                        } else {
                            promptMediaOption()
                        }
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            showRequestDialog()
                        } else {
                            val builder = AlertDialog.Builder(this)
                            builder.setTitle(resources.getString(R.string.go_to_settings_enable_permission))
                            //                                builder.setMessage(String.format(getString(R.string.denied_msg), type));
                            builder.setPositiveButton(resources.getString(R.string.go_to_appsettings)) { dialog, which ->
                                dialog.dismiss()
                                goToSettings()
                            }
                            builder.setNegativeButton(resources.getString(R.string.str_cancel), null)
                            builder.setCancelable(false)
                            builder.show()
                        }
                    }
                }
            }
            5 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                promptMediaOption()
            } else {
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    promptSettings(resources.getString(R.string.camera))
                } else {
                    showRequestDialog()
                }
            }
        }
    }

    private fun init() {
        imgPhoto = findViewById(R.id.imgPhoto)
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etEmail = findViewById(R.id.et_email)
        btnSignup = findViewById(R.id.btn_signup)
    }

    private fun setupDefaults() {
        /*   btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSignupActivity.this, MainPageActivity.class);
                intent.putExtra("from_user",true);
                startActivity(intent);
                finish();
            }
        });*/
    }

    private fun setupEvents() {
        imgPhoto!!.setOnClickListener {
            DeviceUtils.hideSoftKeyboard(this@UserSignupActivity)
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                if (checkAndRequestLocationPermissions()) {
                    promptMediaOption()
                }
            } else {
                promptMediaOption()
            }
        }
        btnSignup!!.setOnClickListener {
            errorFlag = false
            if (etFirstName!!.text.toString().also { firstName = it }.isEmpty()) {
                etFirstName!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etLastName!!.text.toString().also { lastName = it }.isEmpty()) {
                etFirstName!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (etEmail!!.text.toString().also { email = it }.isEmpty()) {
                etFirstName!!.setHintTextColor(resources.getColor(R.color.warning_red))
                errorFlag = true
            }
            if (imagPaths == null) {
                AlertUtils.showToast(this@UserSignupActivity, getString(R.string.str_please_select_the_photo))
                errorFlag = true
            }
            if (!errorFlag) {
                callPostCustomerDetails()
            }

            /*  Intent intent = new Intent(UserSignupActivity.this,MainPageActivity.class);
                intent.putExtra("from_user",true);
                startActivity(intent);*/
        }
    }

    fun callPostCustomerDetails() {
        DeviceUtils.hideSoftKeyboard(this)
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog!!.show()
        val requestBody = HashMap<String, RequestBody>()
        requestBody["firstName"] = createPartFromString(etFirstName!!.text.toString())
        requestBody["lastName"] = createPartFromString(etLastName!!.text.toString())
        requestBody["email"] = createPartFromString(etEmail!!.text.toString())
        requestBody["UserType"] = createPartFromString("C")
        var userPhoto: MultipartBody.Part? = null
        if (imagPaths != null) {
            val file = File(imagPaths!![0])
            val requestFile = RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            userPhoto = MultipartBody.Part.createFormData("uploadImg", file.name, requestFile)
            requestBody["imgName"] = createPartFromString(file.name)
        }
        app.retrofitInterface.postCustDetails("C", userPhoto, requestBody).enqueue(object : RetrofitCallback<ResponseBody?>() {
            override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                super.onSuccessCallback(call, content)
                dialog!!.dismiss()
                Log.e(TAG, content)
                val parser = Gson().fromJson(content, CustUpdateDetailsParser::class.java)
                app.appPreference!!.setAccessToken(parser.userDetails?.accessToken)
                app.appPreference?.setFirstName(parser.userDetails?.firstName)
                app.appPreference?.setLastName(parser.userDetails?.lastName)
                app.appPreference!!.setEmail(parser.userDetails?.email)
                app.appPreference!!.setAppUserId(parser.userDetails?.id)
                if (!parser.error!!) {
                    val intent = Intent(this@UserSignupActivity, MainPageActivity::class.java)
                    intent.putExtra("from_user", true)
                    startActivity(intent)
                }
            }

            override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                super.onFailureCallback(call, message, code)
                dialog!!.dismiss()
                AlertUtils.showAlert(this@UserSignupActivity, message)
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
                app.appPreference!!.setImageUri(uri.toString())
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
        startActivityForResult(intent, AppConstants.Companion.REQUEST_PICTURE_FROM_CAMERA)
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

    private inner class GetPhotoFromGallery(data: Intent) : AsyncTask<Void?, Void?, Void?>() {
        private val data: Intent?
        override fun onPreExecute() {
            super.onPreExecute()
        }

        protected override fun doInBackground(vararg voids: Void?): Void? {
            val f = FileUtils.createNewTempProfileFile(this@UserSignupActivity, "profile").absolutePath
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

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            var photoPath = FileUtils.createNewTempProfileFile(this@UserSignupActivity, "profile").path
            val compressImage = CompressImage(this@UserSignupActivity)
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
    }

    private fun createPartFromString(descriptionString: String): RequestBody {
        var descriptionString = descriptionString
        if (descriptionString.equals("null", ignoreCase = true)) {
            descriptionString = ""
        }
        return RequestBody.create(MULTIPART_FORM_DATA.toMediaTypeOrNull(), descriptionString)
    }

    companion object {
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