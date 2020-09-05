package com.e_comapp.android.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.*

object FileUtils {
    private const val IO_BUFFER_SIZE = 4 * 1024
    private const val TEMP_DIR = "temp"
    private const val TEMP_FILE_NAME = "shared_video.mp4"
    private const val TEMP_IMAGE_FILE = "shared_image.png"
    private const val TAG = "FileUtils"

    /* public static File createTempImageFile(Context context) {
        File f = getTempImageFile(context);
        if (f.exists()) {
            f.delete();
        }
        return f;
    }

    private static File getDirectory(Context context) {
        File dir = new File(context.getExternalCacheDir(), TEMP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getTempImageFile(Context context) {
        return new File(getDirectory(context), TEMP_IMAGE_FILE);
    }

    public static File createTempVideoFile(Context context) {
        File f = getTempVideoFile(context);
        if (f.exists()) {
            f.delete();
        }
        return f;
    }

    public static File getTempVideoFile(Context context) {
        return new File(getDirectory(context), TEMP_FILE_NAME);
    }

    public static String getFileName(Context context, Uri uri, boolean isPicture) {
        String path = isPicture ? getRealPathFromURI_Images(context, uri) : getRealPathFromURI_Videos(context, uri);
        int index = path.lastIndexOf(File.separator);
        if (index != -1 && (index + 1) < path.length()) {
            return path.substring(index + 1);
        }
        return path;
    }

    public static String getFileName(String path) {
        int index = path.lastIndexOf(File.separator);
        if (index != -1 && (index + 1) < path.length()) {
            return path.substring(index + 1);
        }
        return path;
    }

    public static String getRealPathFromURI_Images(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getRealPathFromURI_Videos(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Video.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/
    fun getFileName(path: String): String {
        val index = path.lastIndexOf(File.separator)
        return if (index != -1 && index + 1 < path.length) {
            path.substring(index + 1)
        } else path
    }

    @Throws(IOException::class)
    fun copyStream(source: File?, destination: File?) {
        val inputStream: InputStream = FileInputStream(source)
        val outputStream: OutputStream = FileOutputStream(destination)
        copyStream(inputStream, outputStream)
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

    //    public static String getExtension(String file) {
    //        int startIndex = file.lastIndexOf(".");
    //        if (startIndex != -1) {
    //            return file.substring(file.lastIndexOf("."));
    //        }
    //        return "";
    //    }
    fun getExtension(file: String): String {
        val extensionStartIndex = file.lastIndexOf(".")
        val fileNameStartIndex = file.lastIndexOf(File.separator)
        return if (extensionStartIndex != -1 && extensionStartIndex > fileNameStartIndex) {
            file.substring(file.lastIndexOf("."))
        } else ""
    }

    fun isExtensionAvailable(name: String?): Boolean {
        return !TextUtils.isEmpty(name)
    }

    fun delete(file: File?) {
        if (file == null) {
            return
        }
        if (file.isDirectory) {
            for (f in file.listFiles()) {
                delete(f)
            }
        }
        if (file.exists()) {
            file.delete()
        }
    }

    fun saveTempFile(fileName: String, context: Context, uri: Uri?, cacheFile: File): File? {
        var mFile: File? = null
        val resolver = context.contentResolver
        var `in`: InputStream? = null
        var out: FileOutputStream? = null
        try {
            `in` = resolver.openInputStream(uri)

//            mFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + Config.VIDEO_COMPRESSOR_APPLICATION_DIR_NAME + Config.VIDEO_COMPRESSOR_TEMP_DIR, fileName);
            mFile = File(cacheFile.parent, "temp_$fileName")
            out = FileOutputStream(mFile, false)
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            out.flush()
        } catch (e: IOException) {
            Log.e(TAG, "", e)
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    Log.e(TAG, "", e)
                }
            }
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    Log.e(TAG, "", e)
                }
            }
        }
        return mFile
    }

    fun isImagePath(path: String): Boolean {
        return if (path.endsWith(".png") || path.endsWith(".PNG") || path.endsWith(".jpg") || path.endsWith(".JPG") || path.endsWith(".jpeg") || path.endsWith(".JPEG")) {
            true
        } else {
            false
        }
    }

    fun createNewTempProfileFile(context: Context?, type: String): File {
        val dir = File(context!!.externalCacheDir, type)
        dir.mkdir()
        return File(dir, "tmp_$type.png")
    }
}