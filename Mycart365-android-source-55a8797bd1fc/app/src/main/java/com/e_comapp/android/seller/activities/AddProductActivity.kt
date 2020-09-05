package com.e_comapp.android.seller.activities

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeFile
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.e_comapp.android.R
import com.e_comapp.android.seller.adapters.BannerPagerAdapter
import com.e_comapp.android.seller.api.RetrofitCallback
import com.e_comapp.android.base.BaseActivity
import com.e_comapp.android.utils.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.layout_photo_holder.view.*
import kotlinx.android.synthetic.main.layout_view_photo.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class AddProductActivity: BaseActivity() {
    var pagerAdapter: BannerPagerAdapter? = null
    var linearLayout: LinearLayout? = null
    var count:Int =0
    var countAdded: Int =0
    private var  flag:Boolean = false
    lateinit var externalIntentHandler: ExternalIntentHandler
    private var errorFlag = false
    final val TAG = javaClass.simpleName

     var prdouctName = ""
     var brandName = ""
     var category = ""
     var description = ""
     var productId = ""
     var unitType = ""
     var netStock = ""
     var unitPrice = ""
     var offerPrice = ""
     var expireDate = ""
     var presenceOfIngredients = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        init();
        setupDefaults();
        setupEvents();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AppConstants.REQUEST_CODE_IMAGE_CAPTURE -> {

                    val f = Fileutils2.getTempPictureFile(this)

                    if (f != null) {
                        val file = File(f.absolutePath)

                        val destFile : File=  Fileutils2.createTempFileInMove(this@AddProductActivity,countAdded+1)
                        var  myBitmap = decodeFile(file.absolutePath)
                        var scaledBitmap = DeviceUtils.rotateImageIfRequired(myBitmap, Uri.parse(f.absolutePath))
                        scaledBitmap = DeviceUtils.scaleBitmap(scaledBitmap,600,800)
                        AsyncTaskRunner(scaledBitmap, f.absolutePath).execute()

                    }
                }
                AppConstants.REQUEST_CODE_GALLERY_IMAGE -> {

                    if (data!! == null)
                        return
                    var filePath: String? = null
                    if (data!!.clipData != null) {
                        var item: ClipData.Item = data!!.clipData!!.getItemAt(0)
                        filePath = Fileutils2.getPath(this, item.getUri())
                    } else {
                        filePath = Fileutils2.getPath(this, data!!.data)
                    }


                    val destFile : File=  Fileutils2.createTempFileInMove(this@AddProductActivity,countAdded+1)
                    var  myBitmap = decodeFile(filePath);
                    var scaledBitmap = DeviceUtils.rotateImageIfRequired(myBitmap,Uri.parse(filePath))
                    scaledBitmap = DeviceUtils.scaleBitmap(myBitmap,600,800)
                    val fileOutputStream = FileOutputStream(destFile.absolutePath)
                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 40, fileOutputStream)
                    countAdded += 1
                    addPhotoFromGalleryOrCamera(Uri.parse(destFile.absolutePath),scaledBitmap,destFile.absolutePath)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result: CropImage.ActivityResult = CropImage.getActivityResult(data)

                    Log.d("TEstedFile",""+result.uri+count)
                    val destFile : File=  Fileutils2.createTempFileInMove(this@AddProductActivity,countAdded+1)
                    try {
                        val inputStream = this@AddProductActivity.getContentResolver().openInputStream(result.uri)
                        val fileOutputStream = FileOutputStream(destFile.absolutePath)
                        Fileutils2.copyStream(inputStream, fileOutputStream)
                        fileOutputStream.close()
                        inputStream!!.close()
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val file :File =  File(result.uri.toString())
                    countAdded = countAdded+1
                    addPhotoFromGalleryOrCamera(result.uri,null,destFile.absolutePath)
                }
            }
        }
    }

    private fun init(){


        externalIntentHandler = ExternalIntentHandler(this)
        val list= ArrayList<String>()
        list.add("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQZIPE_3z-Kvyd9mDBqvumGN-5rMQzd0QGiQ-ZE87eZBuPePmxV&usqp=CAU")
        pagerAdapter = BannerPagerAdapter(this, list)
    }

    private fun setupDefaults(){
        setupCustToolbar()
        setMenuIcon(R.drawable.back)
        setCustTitleWithAddress("Title")
        setAddress("Address")
        viewPager.setAdapter(pagerAdapter)
        tabLayout.setupWithViewPager(viewPager)

        addPhotoItem()
    }

    private fun setupEvents(){
        btn_add_product.setOnClickListener(View.OnClickListener {
            errorFlag = false
           if (et_product_name.getText().toString().isEmpty()) {
                et_product_name.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }

            if (et_brand_name.getText().toString().isEmpty()) {
                et_brand_name.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (et_category.getText().toString().isEmpty()) {
                et_category.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (et_descripition.getText().toString().isEmpty()) {
                et_descripition.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (et_product_id.getText().toString().isEmpty()) {
                et_product_id.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (tv_unit_type.getText().toString().isEmpty()) {
                tv_unit_type.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (et_net_stcok.getText().toString().isEmpty()) {
                et_net_stcok.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (et_unit_price.getText().toString().isEmpty()) {
                et_unit_price.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (et_office_price.getText().toString().isEmpty()) {
                et_office_price.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (et_expire_date.getText().toString().isEmpty()) {
                et_expire_date.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            if (et_indgredients.getText().toString().isEmpty()) {
                et_indgredients.setHintTextColor(ContextCompat.getColor(this,R.color.warning_red))
                errorFlag = true
            }
            Log.e(TAG,""+errorFlag)
            if(!errorFlag){
                callPostSellerAddProduct()
            }
        })
    }

    private fun constructJson(): JsonObject? {
        val obj = JsonObject()
        val jsonObject = JsonObject()
        val jsonArray = JsonArray()
        try {
            jsonObject.addProperty("unitType", tv_unit_type.text.toString())
            jsonObject.addProperty("netStock", et_net_stcok.text.toString())
            jsonObject.addProperty("offerPrice", et_office_price.text.toString())
            jsonObject.addProperty("unitPrice", et_unit_price.text.toString())
            jsonArray.add(jsonObject)
            obj.add("unitDetails",jsonArray)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return obj
    }

    fun callPostSellerAddProduct() {
        DeviceUtils.hideSoftKeyboard(this)
        if (!DeviceUtils.isInternetConnected(this)) {
            AlertUtils.showNoInternetConnection(this)
            return
        }
        dialog?.show()
        val requestBody = HashMap<String, RequestBody>()
        requestBody["prodName"] = createPartFromString(et_product_name.text.toString())
        requestBody["brand"] = createPartFromString(et_brand_name.text.toString())
        requestBody["category"] = createPartFromString(et_category.text.toString())
        requestBody["description"] = createPartFromString(et_descripition.text.toString())
        requestBody["expiryDate"] = createPartFromString(et_expire_date.text.toString())
        requestBody["ingredientType"] = createPartFromString(et_indgredients.text.toString())
        requestBody["unitDetails"] = createPartFromString(constructJson().toString())

        var mapImages= ArrayList<MultipartBody.Part>()
        var mapImage = arrayOfNulls<MultipartBody.Part>(countAdded)
        var photo1: MultipartBody.Part? = null
        for(i in 0 until countAdded){
           /* Log.e(TAG,"i"+i+" count"+countAdded)
            if((Fileutils2.createTempFileInMove(this@AddProductActivity,i+1).absolutePath!=null)){
                Log.e(TAG,"for i"+i+" count"+countAdded)
                Log.d("HashMapString iterate",""+Fileutils2.getFileSizeInMb(Fileutils2.createTempFileInMove(this@AddProductActivity,i+1).absolutePath))
                photo1 = createPartFromFile(Fileutils2.createTempFileInMove(this@AddProductActivity,i+1).absolutePath,"uploadImg[]")
                mapImage[i] = photo1
            }*/
                if((Fileutils2.createTempFileInMove(this@AddProductActivity,i+1).absolutePath!=null)){
                    var file = Fileutils2.createTempFileInMove(this@AddProductActivity,i+1)
                    val requestFile = okhttp3.RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
                    mapImage[i] = MultipartBody.Part.createFormData("uploadImg[]", file.getName(), requestFile)
           }

         /*   if((Fileutils2.createTempFileInMove(this@AddProductActivity,i+1).absolutePath!=null)){
                var file = createPartFromFile(Fileutils2.createTempFileInMove(this@AddProductActivity,i+1).absolutePath,"uploadImg")
                mapImage[i] = file
            }*/

        }

       app.retrofitInterface.postSellerAddProduct("S",mapImage,requestBody)?.enqueue(object : RetrofitCallback<ResponseBody?>(){
           override fun onSuccessCallback(call: Call<ResponseBody?>?, content: String?) {
               super.onSuccessCallback(call, content)
               dialog?.hide()
               Log.e(TAG, content)
               val jsonObject = JSONObject(content)
               val error = jsonObject.getBoolean("error");
               val message = jsonObject.getString("message");
               if(!error){
                   AlertUtils.showToast(this@AddProductActivity,"Added Successfully")
                   finish()
               }
           }


           override fun onFailureCallback(call: Call<ResponseBody?>?, message: String?, code: Int) {
               super.onFailureCallback(call, message, code)
               dialog?.hide()
               AlertUtils.showAlert(this@AddProductActivity, message)
           }
       })
    }

    private fun addPhotoItem() {
        linearLayout = LinearLayout(this)
        linearLayout!!.layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_photo_holder, null)
        var photoHolder = dialogView.llAddPhoto
        photoHolder.setOnClickListener {
            if(linearLayout!!.childCount<6) {
                flag = true
                externalIntentHandler.promptMediaOption()
            }else
                AlertUtils.showToast(this@AddProductActivity,"You can add only 5 images")
        }
        hsPhotoHolder.addView(linearLayout)
        linearLayout!!.addView(dialogView)
    }




    private fun addPhotoFromGalleryOrCamera(uri: Uri, bmp: Bitmap?, desPath : String?) {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_view_photo, null)
        var imgClose = dialogView.imgClose
        val imgPhotoView = dialogView.imgViewPhoto
        imgClose.setOnClickListener {
            linearLayout!!.removeView(dialogView)
            if(File(desPath).exists())
                File(desPath).delete()
            count= linearLayout!!.childCount
            if(countAdded>0)
                countAdded = countAdded-1
        }
        // imgPhotoView.setImageURI(uri.toString())
        imgPhotoView.setImageBitmap(bmp)
        linearLayout!!.addView(dialogView)
    }




    private fun addPhotoFromGalleryOrCameraForEdit(uri: String,pos: Int) {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_view_photo, null)
        dialogView.setTag(linearLayout!!.childCount)
        var imgClose = dialogView.imgClose
        val imgPhotoView = dialogView.imgViewPhoto
        imgClose.setOnClickListener {

            linearLayout!!.removeView(dialogView)
            if(countAdded>0)
                countAdded = countAdded-1
            count= linearLayout!!.childCount
//            deletedIdList!!.add(""+moveDetails!!.items[0].images.get(pos).image_id)
        }
        imgPhotoView.setImageURI(uri.toString())
        linearLayout!!.addView(dialogView)
    }

    private fun dopostExecuteTask(result: Bitmap, filePath: String) {
        val destFile : File =  Fileutils2.createTempFileInMove(this@AddProductActivity,countAdded+1)
        countAdded = countAdded+1
        addPhotoFromGalleryOrCamera(Uri.parse(destFile.absolutePath),result,destFile.absolutePath)
    }

    private val MULTIPART_FORM_DATA = "text/plain"
    private fun createPartFromString(descriptionString: String?): RequestBody {
        return RequestBody.create(MULTIPART_FORM_DATA.toMediaTypeOrNull(), descriptionString!!)
    }
    private fun createPartFromFile(imagePath: String, fileName: String): MultipartBody.Part?{
        var userPhoto: MultipartBody.Part? = null
        if (imagePath != null) {
            val file = File(imagePath)
            val requestFile = okhttp3.RequestBody.create("image/jpg".toMediaTypeOrNull(), file)
            userPhoto = MultipartBody.Part.createFormData(fileName, file.getName(), requestFile)
            return userPhoto
        }
        return null
    }

    private inner class AsyncTaskRunner(var bitmap: Bitmap?, var filePath: String) : AsyncTask<String, String, Bitmap>() {
        override fun doInBackground(vararg params: String): Bitmap? {
            val destFile : File=  Fileutils2.createTempFileInMove(this@AddProductActivity,countAdded+1)



            val fileOutputStream = FileOutputStream(destFile.absolutePath)
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 40, fileOutputStream);


            return  bitmap;

        }


        override fun onPostExecute(result: Bitmap) {
            // execution of result of Long time consuming operation
            dialog!!.dismiss()
            dopostExecuteTask(result,filePath)

        }


        override fun onPreExecute() {
            dialog!!.show()
        }


        override fun onProgressUpdate(vararg text: String) {

        }

    }
}