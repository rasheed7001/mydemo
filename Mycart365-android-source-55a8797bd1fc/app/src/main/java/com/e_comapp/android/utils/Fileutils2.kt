package com.e_comapp.android.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.*

object Fileutils2 {
    var currentPhotoFile: File? = null
    fun createTempFile(context: Context): File? {
        try {
            currentPhotoFile = File(getPictureDir(context), System.currentTimeMillis().toString() + ".jpg")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentPhotoFile
    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        output.flush()
        output.close()
        input.close()
    }

    fun createNewTempProfileFile(context: Context, type: String): File {
        val dir = File(context.externalCacheDir, type)
        dir.mkdir()
        return File(dir, "tmp_$type.png")
    }

    fun getPictureDir(context: Context): File {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    @Throws(FileNotFoundException::class, IOException::class)
    fun copyFile(sourceLocation: File?, targetLocation: File?) {
        val `in`: InputStream = FileInputStream(sourceLocation)
        val out: OutputStream = FileOutputStream(targetLocation)

        // Copy the bits from instream to outstream
        val buf = ByteArray(1024)
        var len: Int
        while (`in`.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        `in`.close()
        out.close()
    }

    fun createTempFileInMove(context: Context, itemnumber: Int): File {
        var myMoves: File? = null
        myMoves = File(context.externalCacheDir, "myMoves")
        myMoves.mkdir()

        /* File movenumberFolder = null;
    movenumberFolder= new File(myMoves, movenumber);
        movenumberFolder.mkdir();*/
        return File(myMoves, "$itemnumber.png")
    }

    fun deleteMoveImage(context: Context, movenumber: String, itemnumber: Int) {
        var myMoves: File? = null
        myMoves = File(context.externalCacheDir, "myMoves/$movenumber/$itemnumber")
        if (myMoves.exists()) myMoves.delete()
    }

    fun deleteFile(uri: String?) {
        val currentFile = File(uri)
        val files = currentFile.listFiles()
        for (i in files.indices) {
            if (files[i].isDirectory) {
                files[i].delete()
            }
            //no else, or you'll never get rid of this folder!
            files[i].delete()
        }
    }

    fun deleteMove(context: Context, movenumber: String) {
        var myMoves: File? = null
        myMoves = File(context.externalCacheDir, "/myMoves/$movenumber")
        if (myMoves.exists()) myMoves.delete()
    }

    fun createImageProfile(context: Context, type: String): File {
        var mapdir: File? = null
        var f: File? = null
        if (type.equals("photo", ignoreCase = true)) {
            mapdir = File(context.externalCacheDir, "profile")
        } else if (type.equals("crop", ignoreCase = true)) {
            mapdir = File(context.externalCacheDir, "crop")
        }
        mapdir!!.mkdir()
        f = File(mapdir.toString() + File.separator, "photo.png")
        return f
    }

    val imagePath: String
        get() = currentPhotoFile!!.absolutePath

    val fileName: String
        get() = currentPhotoFile!!.absolutePath.substring(currentPhotoFile!!.absolutePath.lastIndexOf('/') + 1)

    fun getTempPictureFile(context: Context): File? {
        var max: Long = 0
        var result: String? = null
        for (file in getPictureDir(context).listFiles()) {
            val path = file.absolutePath
            val fname = file.name
            val name = fname.substring(0, fname.indexOf("."))
            val date = name.toLong()
            if (date > max) {
                max = date
                result = path
            }
        }
        return result?.let { File(it) }
    }

    fun getFileSizeInMb(path: String?): Long {
        try {
            val file = File(path)
            var length = file.length()
            length = length / 1024
            println("File Path : " + file.path + ", File size : " + length + " KB")
            return length
        } catch (e: Exception) {
            println("File not found : " + e.message + e)
        }
        return -1
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                        split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else getDataColumn(context, uri, null, null)
            /****
             * end
             */
//            return getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun isGoogleKeep(uri: Uri): Boolean {
        return "com.google.android.keep" == uri.authority
    }

    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
                column
        )
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs,
                    null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }
}