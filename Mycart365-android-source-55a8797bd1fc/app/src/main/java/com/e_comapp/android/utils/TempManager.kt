package com.e_comapp.android.utils

import android.content.Context
import android.os.SystemClock
import java.io.File

object TempManager {
    private const val IO_BUFFER_SIZE = 4 * 1024
    private const val TEMP_DIR = "temp"
    private const val TEMP_FILE_NAME = "VideoAndroid.mp4"
    private const val TEMP_IMAGE_FILE = "PictureAndroid.jpg"
    private const val TEMP_DOCUMENT = "DocumentAndroid.pdf"
    private const val TEMP_AUDIO = "AudioAndroid.mp3"
    private const val TEMP_AUDIO_AMR = "AudioAndroid.amr"
    private const val COMMON_FILE = "cmndir"
    private const val PIC_DIR = "3"

    /**
     * Note: this is delete existing and create new file
     */
    fun createTempImageFile(context: Context?): File {
        val f = getTempImageFile(context)
        if (f.exists()) {
            f.delete()
        }
        return f
    }

    fun createTempPictureFile(context: Context?): File {
        val dir = File(getDirectory(context), PIC_DIR)
        dir.mkdirs()
        val name = System.currentTimeMillis().toString()
        return File(dir, "$name.jpg")
    }

    fun getTempPictureFile(context: Context?): File? {
        val dir = File(getDirectory(context), PIC_DIR)
        var max: Long = 0
        var result: String? = null
        for (file in dir.listFiles()) {
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

    private fun getDirectory(context: Context?): File {
        // File dir = new File(context.getExternalCacheDir(), TEMP_DIR);
//        Log.d("ss","temp file create 3");
        val dir = File(context!!.externalCacheDir, TEMP_DIR)
        dir.mkdirs()
        //        Log.d("ss","temp file create 3 end");
        return dir
    }

    fun getTempFile(context: Context?): File {
        return File(getDirectory(context), "_tmp")
    }

    fun getTempAudioFile(context: Context?): File {
        return File(getDirectory(context), TEMP_AUDIO)
    }

    fun getTempAudioAMR(context: Context?): File {
        return File(getDirectory(context), TEMP_AUDIO_AMR)
    }

    fun createTempAMRAudioFile(context: Context?): File {
        val file = getTempAudioAMR(context)
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    fun createTempAudioFile(context: Context?): File {
        val file = getTempAudioFile(context)
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    fun getTempImageFile(context: Context?): File {
        return File(getDirectory(context), TEMP_IMAGE_FILE)
    }

    fun getTempPdfDocumentFile(context: Context?): File {
        return File(getDirectory(context), TEMP_DOCUMENT)
    }

    fun createNewDocumentFile(context: Context?): File {
        val file = getTempPdfDocumentFile(context)
        if (file.exists()) {
            file.delete()
        }
        return file
    }

    //public static File createNewDrawingFile(C)
    fun createTempVideoFile(context: Context?): File {
        val f = getTempVideoFile(context)
        if (f.exists()) {
            f.delete()
        }
        return f
    }

    fun createTempVideoFile(context: Context?, tempFileName: String?): File {
//        Log.d("ss","temp file create 1");
        val f = getTempVideoFile(context, tempFileName)
        if (f.exists()) {
            f.delete()
        }
        //        Log.d("ss","temp file create 1 end");
        return f
    }

    fun getTempVideoFile(context: Context?): File {
        return File(getDirectory(context), TEMP_FILE_NAME)
    }

    fun getTempVideoFile(context: Context?, tempFileName: String?): File {
//        Log.d("ss","temp file create 2");
        return File(getDirectory(context), tempFileName)
    }

    private fun getTempShareDir(context: Context): File {
        val dir = File(getDirectory(context), "tmp_share")
        dir.mkdirs()
        return dir
    }

    fun getTempShareFile(context: Context, ext: String): File {
        return File(getTempShareDir(context), "tooteet_share$ext")
    }

    fun createTempShareFile(context: Context, ext: String): File {
        FileUtils.delete(getTempShareDir(context))
        /*if (file.exists()) {
            file.delete();
        }*/return getTempShareFile(context, ext)
    }

    fun getVideoThumbFile(context: Context?, fileName: String): File? {
        for (i in 0 until Int.MAX_VALUE) {
            val thumb = File(getDirectory(context), fileName + "_" + i + ".png")
            if (!thumb.exists()) {
                return thumb
            }
        }
        return null
    }

    fun getThumbFile(context: Context?, fileName: String): File? {
        var fileName = fileName
        if (TextUtils.isEmpty(fileName)) {
            fileName = "Thumb"
        }
        for (i in 0 until Int.MAX_VALUE) {
            if (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.indexOf("."))
            }
            val thumb = File(getDirectory(context), fileName + "_" + i + ".png")
            if (!thumb.exists()) {
                return thumb
            }
        }
        return null
    }

    fun clearTempDir(context: Context?) {
        val dir = getDirectory(context)
        for (f in dir.listFiles()) {
            if (f.exists()) {
                f.delete()
            }
        }
        val picDir = File(dir, PIC_DIR)
        if (picDir.exists()) {
            for (file in picDir.listFiles()) {
                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }

    fun createCommonFile(context: Context?, encryptedFileName: String?): File {
        val dir = File(getDirectory(context), COMMON_FILE)
        dir.mkdir()
        if (dir.list() != null && dir.list().size != 0) {
            for (f in dir.listFiles()) {
                f.delete()
            }
        }
        return File(dir, encryptedFileName)
    }

    fun createTempVideoFileForCamera(context: Context?): File {
        val f = getTempVideoFileForCamera(context)
        if (f.exists()) {
            f.delete()
        }
        return f
    }

    fun getTempVideoFileForCamera(context: Context?): File {
        return File(getDirectory(context), SystemClock.currentThreadTimeMillis().toString() + TEMP_FILE_NAME)
    }
}