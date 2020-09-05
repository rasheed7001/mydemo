package com.e_comapp.android.user.fragments

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseFragment
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.user.activities.MainPageActivity
import com.e_comapp.android.user.models.CustUpdateDetailsParser
import com.e_comapp.android.utils.*
import com.e_comapp.android.views.CustomBtn
import com.e_comapp.android.views.CustomEditText
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [MyAccountUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccountUserFragment : BaseFragment() {
    private var etFirstName: CustomEditText? = null
    private var etLastName: CustomEditText? = null
    private var etEmail: CustomEditText? = null
    private var etMobile: CustomEditText? = null
    private var btnUpdate: CustomBtn? = null
    private var imgEditFirstName: ImageView? = null
    private var imgEditLastName: ImageView? = null
    private var imgEditEmail: ImageView? = null
    private var imgProfile: SimpleDraweeView? = null
    private var errorFlag = false
    private var imagPaths: Array<String?>? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private val mobileNumber: String? = null
    var isEdited = false

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setLeftMenuIcon(R.drawable.ic_left_menu)
        setTitle(getString(R.string.str_my_account))
    }

    override fun menuClicks() {
        super.menuClicks()
        (activity as MainPageActivity?)!!.drawer!!.openDrawer(GravityCompat.START)
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
        return inflater.inflate(R.layout.fragment_my_account_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
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
                val f = TempManager.getTempPictureFile(context)
                if (f != null) {
                    var path = f.absolutePath
                    val compressImage = CompressImage(context)
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
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                                requestPermissions(AppConstants.Companion.PERMISSIONS, 5)
                            }
                        } else {
                            promptMediaOption()
                        }
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                            showRequestDialog()
                        } else {
                            val builder = AlertDialog.Builder(activity)
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
                if (!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
                    promptSettings(resources.getString(R.string.camera))
                } else {
                    showRequestDialog()
                }
            }
        }
    }

    private fun init(view: View) {
        etFirstName = view.findViewById(R.id.et_first_name)
        etLastName = view.findViewById(R.id.et_last_name)
        etEmail = view.findViewById(R.id.et_email)
        etMobile = view.findViewById(R.id.et_mobile)
        btnUpdate = view.findViewById(R.id.btnUpdate)
        imgEditEmail = view.findViewById(R.id.ic_edit_email)
        imgEditFirstName = view.findViewById(R.id.ic_edit_first_name)
        imgEditLastName = view.findViewById(R.id.ic_edit_lastname)
        imgProfile = view.findViewById(R.id.imgProfile)
    }

    private fun setupDefaults() {
        imgProfile!!.setImageURI(app.appPreference?.getImageUri())
        etFirstName!!.setText(app.appPreference?.getFirstName())
        etLastName!!.setText(app.appPreference?.getLastName())
        etEmail!!.setText(app.appPreference?.getEmail())
        etMobile!!.setText(app.appPreference?.getMobileNumber())
    }

    private fun setupEvents() {
        imgProfile!!.setOnClickListener {
            DeviceUtils.hideSoftKeyboard(activity)
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                if (checkAndRequestLocationPermissions()) {
                    promptMediaOption()
                }
            } else {
                promptMediaOption()
            }
        }
        imgEditFirstName!!.setOnClickListener {
            etFirstName!!.isEnabled = true
            etFirstName!!.setSelection(etFirstName!!.text!!.length)
            isEdited = true
            btnUpdate!!.isSelected = true
        }
        imgEditLastName!!.setOnClickListener {
            etLastName!!.isEnabled = true
            etLastName!!.setSelection(etLastName!!.text!!.length)
            isEdited = true
            btnUpdate!!.isSelected = true
        }
        imgEditEmail!!.setOnClickListener {
            etEmail!!.isEnabled = true
            etEmail!!.setSelection(etEmail!!.text!!.length)
            isEdited = true
            btnUpdate!!.isSelected = true
        }
        btnUpdate!!.setOnClickListener {
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
            if (!errorFlag && btnUpdate!!.isSelected) callPostCustomerDetails()
        }
    }

    fun callPostCustomerDetails() {
        DeviceUtils.hideSoftKeyboard(activity)
        if (!DeviceUtils.isInternetConnected(context)) {
            AlertUtils.showNoInternetConnection(context)
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
            app.appPreference!!.setImageName(file.name)
        }
        if (userPhoto != null) {
            app.retrofitInterface.postCustDetails("C", userPhoto, requestBody).enqueue(object : RetrofitCallback<ResponseBody?>() {
                override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                    super.onSuccessCallback(call, content)
                    dialog!!.dismiss()
                    val parser = Gson().fromJson(content, CustUpdateDetailsParser::class.java)
                    app.appPreference!!.setAccessToken(parser.userDetails?.accessToken)
                    app.appPreference!!.setFirstName(parser.userDetails?.firstName)
                    app.appPreference!!.setLastName(parser.userDetails?.lastName)
                    app.appPreference!!.setEmail(parser.userDetails?.email)
                    app.appPreference!!.setAppUserId(parser.userDetails?.id)
                    if (!parser.error!!) {
                    }
                }

                override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                    super.onFailureCallback(call, message, code)
                    dialog!!.dismiss()
                    AlertUtils.showAlert(context, message)
                }
            })
        } else {
            requestBody["imgName"] = createPartFromString(app.appPreference?.getImageName())
            app.retrofitInterface.postCustDetails("C", requestBody).enqueue(object : RetrofitCallback<ResponseBody?>() {
                override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
                    super.onSuccessCallback(call, content)
                    dialog!!.dismiss()
                    val parser = Gson().fromJson(content, CustUpdateDetailsParser::class.java)
                    app.appPreference!!.setAccessToken(parser.userDetails?.accessToken)
                    app.appPreference!!.setFirstName(parser.userDetails?.firstName)
                    app.appPreference!!.setLastName(parser.userDetails?.lastName)
                    app.appPreference!!.setEmail(parser.userDetails?.email)
                    app.appPreference!!.setAppUserId(parser.userDetails?.id)
                    if (!parser.error!!) {
                    }
                }

                override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
                    super.onFailureCallback(call, message, code)
                    dialog!!.dismiss()
                    AlertUtils.showAlert(context, message)
                }
            })
        }
    }

    private fun checkAndRequestLocationPermissions(): Boolean {
        val storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), listPermissionsNeeded.toTypedArray(), AppConstants.Companion.REQUEST_PERMISSION_READ_STORAGE)
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
                imgProfile!!.setImageURI(uri)
                app.appPreference?.setImageUri(uri.toString())
                btnUpdate!!.isSelected = true
            }
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            Toast.makeText(context, result.error.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCrop(file: File) {
        CropImage.activity(Uri.fromFile(file))
                .setActivityTitle("Profile image crop")
                .setAllowFlipping(false)
                .setAllowRotation(false)
                .start(requireActivity())
    }

    private fun promptMediaOption() {
        val ITEMS = arrayOf("Take Picture", "Choose Image")
        openOptionDialog(context, ITEMS, "" + getString(R.string.app_name), DialogInterface.OnClickListener { dialog, which ->
            if (which == 0) {
                openCamera()
            } else {
                openGallery()
            }
        })
    }

    private fun openCamera() {
        val filePath = TempManager.createTempPictureFile(context).absolutePath
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(filePath)))
        } else {
            val file = File(filePath)
            val photoUri = FileProvider
                    .getUriForFile(requireContext().applicationContext, requireActivity().applicationContext.packageName + ".provider", file)
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
        val builder = AlertDialog.Builder(context)
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
        val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + requireActivity().packageName))
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
        AlertDialog.Builder(context)
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
            val f = FileUtils.createNewTempProfileFile(context, "profile").absolutePath
            if (data != null) {
                try {
                    val inputStream = activity!!.contentResolver.openInputStream(data.data)
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
            var photoPath = FileUtils.createNewTempProfileFile(context, "profile").path
            val compressImage = CompressImage(context)
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

    private fun createPartFromString(descriptionString: String?): RequestBody {
        var descriptionString = descriptionString
        if (descriptionString.equals("null", ignoreCase = true)) {
            descriptionString = ""
        }
        return RequestBody.create(MULTIPART_FORM_DATA.toMediaTypeOrNull(), descriptionString!!)
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
         * @return A new instance of fragment MyAccountUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): MyAccountUserFragment {
            val fragment = MyAccountUserFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }

        private fun openOptionDialog(context: Context?, items: Array<String>, title: String, positiveClick: DialogInterface.OnClickListener) {
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