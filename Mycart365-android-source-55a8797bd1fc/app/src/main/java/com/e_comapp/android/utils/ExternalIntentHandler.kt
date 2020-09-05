package com.e_comapp.android.utils

import android.Manifest
import android.os.Build
import android.content.Context
import android.content.Intent
import android.widget.TextView
import android.view.LayoutInflater
import android.content.pm.PackageManager
import android.view.View
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore

import java.io.File
import android.content.Intent.getIntent
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.e_comapp.android.R
import com.e_comapp.android.base.BaseActivity


class ExternalIntentHandler(baseActivity: BaseActivity) {

    private val TAG = javaClass.simpleName

    private val PERMISSION = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val baseActivity by lazy  {
        baseActivity
    }

    private fun checkExternalStoragePermission(context: BaseActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.checkSelfPermission(PERMISSION) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun openGallery(){
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryIntent.type = "image/*"

        var intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent = Intent.createChooser(intent,"choose image")



      /*  val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image*//*"

        val chooserIntent = Intent.createChooser(galleryIntent, "Select Image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
*/
        baseActivity.startActivityForResult(intent, AppConstants.REQUEST_CODE_GALLERY_IMAGE)
    }

    private fun openCamera() {
        val currentImageFile = Fileutils2.createTempFile(baseActivity)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile))
        } else {
            val photoUri = currentImageFile?.let { FileProvider.getUriForFile(baseActivity, baseActivity.packageName + ".provider", it) }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        baseActivity.startActivityForResult(intent, AppConstants.REQUEST_CODE_IMAGE_CAPTURE)
    }

    fun promptMediaOption() {
        if (checkExternalStoragePermission(baseActivity)) {
            val imageDrawable = baseActivity.resources.getDrawable(R.drawable.ic_menu_camera)
            val galleryDrawable = baseActivity.resources.getDrawable(R.drawable.ic_menu_gallery)

            val ITEMS = arrayOf(
                    Item(R.string.take_picture, imageDrawable),
                    Item(R.string.choose_image, galleryDrawable))

            openPictureOptionDialog(baseActivity, ITEMS, "Choose Media", View.OnClickListener {
                if (it.tag != null)
                    (it.tag as AlertDialog).dismiss()
                when (it.id) {
                    R.id.textcamera -> {
                        openCamera()
                    }
                    R.id.textgallery -> {
                        openGallery()
                    }
                }
            })
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                baseActivity.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA),
                    AppConstants.REQUEST_WRITE_EXTERNAL_STORAGE)

            }

        }
        }
    }

    private fun openPictureOptionDialog(context: Context, items: Array<Item>, title: String, positiveClick: View.OnClickListener) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val alertLayout = inflater.inflate(R.layout.profile_image_select_dialog_item, null)

        val camera: TextView = alertLayout.findViewById<TextView>(R.id.textcamera) as TextView
        val item = items[0]
        camera.setText(item.string)
        camera.textSize = 16f
        camera.setCompoundDrawablesWithIntrinsicBounds(item.drawable, null, null, null)
        camera.compoundDrawablePadding = DeviceUtils.getPixelFromDp(context, 15)
        camera.setOnClickListener(positiveClick)

        val gallery: TextView = alertLayout.findViewById<TextView>(R.id.textgallery) as TextView
        val item1 = items[1]
        gallery.setText(item1.string)
        gallery.textSize = 16f
        gallery.setCompoundDrawablesWithIntrinsicBounds(item1.drawable, null, null, null)
        gallery.compoundDrawablePadding = DeviceUtils.getPixelFromDp(context, 15)
        gallery.setOnClickListener(positiveClick)

        val builder = AlertUtils.getBuilder(context)
        builder.setTitle(title)
        builder.setView(alertLayout)
        val alertDialog = builder.create()
        alertDialog.show()

        camera.tag = alertDialog
        gallery.tag = alertDialog
        alertDialog.setOnDismissListener { }
    }

    internal class Item(var string: Int, var drawable: Drawable)
