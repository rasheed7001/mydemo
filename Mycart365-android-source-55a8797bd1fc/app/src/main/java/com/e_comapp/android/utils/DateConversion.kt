package com.e_comapp.android.utils

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateConversion {
    fun getDate(ourDate: String?): String? {
        var ourDate = ourDate
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val value = formatter.parse(ourDate)
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //this format changeable
            dateFormatter.timeZone = TimeZone.getDefault()
            ourDate = dateFormatter.format(value)

            //Log.d("ourDate", ourDate);
        } catch (e: Exception) {
            ourDate = "00-00-0000 00:00"
        }
        return ourDate
    }

    fun getDateFromString(dateInString: String?, actualformat: String?, exceptedFormat: String?): String? {
        val form = SimpleDateFormat(actualformat, Locale("en", "EN"))
        var formatedDate: String? = null
        val date: Date
        try {
            date = form.parse(dateInString)
            val postFormater = SimpleDateFormat(exceptedFormat, Locale.getDefault())
            formatedDate = postFormater.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return formatedDate
    }

    fun isToday(millis: Long): Boolean {
        return isDayEqual(0, millis)
    }

    fun isYesterday(millis: Long): Boolean {
        return isDayEqual(-1, millis)
    }

    fun isTomorrow(millis: Long): Boolean {
        return isDayEqual(1, millis)
    }

    private fun isDayEqual(dayToAdd: Int, millis: Long): Boolean {
        val c1 = Calendar.getInstance()
        c1.add(Calendar.DAY_OF_YEAR, dayToAdd)
        val c2 = Calendar.getInstance()
        c2.timeInMillis = millis
        return (c1[Calendar.YEAR] == c2[Calendar.YEAR]
                && c1[Calendar.DAY_OF_YEAR] == c2[Calendar.DAY_OF_YEAR])
    }

    fun stringToDate(strDate: String?, parseFormat: String?): Date? {
        val formatter: DateFormat
        var date: Date? = null
        formatter = SimpleDateFormat(parseFormat, Locale.getDefault())
        date = try {
            formatter.parse(strDate) as Date
        } catch (e: ParseException) {
            Date(strDate)
        }
        return date
    }

    fun calendarToStringwithslash(cal: Calendar): String {
        val df = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
        return df.format(cal.time)
    }

    fun getPreviousMonth(cal: Calendar): Calendar {
        cal[Calendar.MONTH] = cal[Calendar.MONTH] - 1
        return cal
    }

    fun getNextMonth(cal: Calendar): Calendar {
        cal[Calendar.MONTH] = cal[Calendar.MONTH] + 1
        return cal
    }

    fun getTimeFromLong(time: Long, format: String?): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(Date(time))
    }

    /**
     * Will display full date eg. Friday, 30 October 2015
     */
    fun getFullDate(time: Long): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.FULL)
        return dateFormat.format(Date(time))
    }

    fun getTimeFromString(dateTime: String?, format: String?): Long {
//        String someDate = "05.10.2011";
//        SimpleDateFormat sdf = new SimpleDateFormat(format);
//        Date date = sdf.parse(dateTime);
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        try {
            val date = sdf.parse(dateTime)
            return date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getTimeFromDate(date: Date?, format: String?): String {
        return getTimeFromDate(date, format, TimeZone.getDefault())
    }

    fun getTimeFromDate(date: Date?, format: String?, timeZone: TimeZone?): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        if (timeZone != null) {
            sdf.timeZone = timeZone
        }
        return sdf.format(date)
    }

    fun getCurrentTime(timeZone: String?): Long {
        val sdf = SimpleDateFormat()
        sdf.timeZone = TimeZone.getTimeZone(timeZone)
        return sdf.calendar.timeInMillis
    }

    /**
     * The time(long) value is seconds not millis
     *
     * @param timeZone String representation of time format
     * @param time     time as long value in seconds
     * @return time time as long in seconds
     */
    // GMT0
    fun getLocalizedTime(timeZone: String?, time: String?, format: String?): Long {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(timeZone)
        var millist: Long = 0
        try {
            val date = sdf.parse(time)
            millist = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return millist
    }

    /**
     * This is for GLOBAL
     */
    fun getTimeForGMT0(time: String?, format: String?): Long {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT0")
        var millist: Long = 0
        try {
            val date = sdf.parse(time)
            millist = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return millist
    }

    /**
     * This is for ony Mingl App 2014-05-19 11:38:03
     */
    /*
     * public static String getTimeForApp(String time, String requiredFormat) {
     * SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
     * Locale.US); sdf.setTimeZone(TimeZone.getTimeZone("GMT0")); Date date =
     * null; try { date = sdf.parse(time); } catch (ParseException e) {
     * e.printStackTrace(); } return new SimpleDateFormat(requiredFormat,
     * Locale.getDefault()).format(date); }
     */
    fun getDateDiff(time: Long, current: Long): Long {
        val diff = Math.max(time, current) - Math.min(time, current)
        return diff / (1000 * 60 * 60 * 24)
    }

    /**
     * This is for ony Mingl App 2014-05-19 11:38:03
     */
    fun convertToMillis(time: String?): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        var millis = 0L
        try {
            val date = sdf.parse(time)
            millis = date.time
        } catch (e: ParseException) {
            //e.printStackTrace();
        }
        return millis
    }

    /**
     * It converts to GMT-0
     */
    fun getTimeMillisForApp(time: String?): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("GMT0")
        var millis = 0L
        try {
            val date = sdf.parse(time)
            millis = date.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return millis
    }

    /**
     * @param timeZone
     * @param time
     * @return
     */
    fun getLocalizedTime(timeZone: String?, time: Long): Long {
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(timeZone)
        val dateS = sdf.format(Date(time)) // /1351330745
        return stringToDate(dateS, "dd-MM-yyyy hh:mm:ss aaa")!!.time
    }

    //    public static long getLocalizedDate(String date){
    //        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    //        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    //        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa", Locale.getDefault());
    //        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
    //        String dateS = sdf.format(new Date(time)); // /1351330745
    //        return DateConversion.stringToDate(dateS, "dd-MM-yyyy hh:mm:ss aaa").getTime();
    //    }
    fun getDateWithTFromMilliSeconds(timeMilliSecs: Long, dateMilliSecs: Long): String {
        var dateString = ""
        try {
            if (timeMilliSecs == -1L) {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateString = format.format(Date(dateMilliSecs))
                val parsedDate = format.parse(dateString)
                dateString = formatter.format(parsedDate)
            } else {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                dateString = formatter.format(Date(timeMilliSecs))
            }
            //            Log.d("TAG", "getDateWithTFromMilliSeconds  --  convertedDateString: "+dateString);
        } catch (e: Exception) {
//            Log.d("TAG", "getDateWithTFromMilliSeconds    Exception: "+e.toString());
        }
        return dateString
    }

    fun getDateAndTimeWithoutGMT(date: String, finalFormat: String?): String? {
        var finalFormat = finalFormat
        var formattedDateString: String? = null
        if (!TextUtils.isEmpty(date)) {
            if (date.contains("T00:00:00")) {
                finalFormat = "MMMM dd, yyyy"
            }
            val format1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format2 = SimpleDateFormat(finalFormat)
            if (!TextUtils.isEmpty(date) && !date.equals("-1", ignoreCase = true)) {
                try {
                    // format1.setTimeZone(TimeZone.getTimeZone("GMT0"));
                    val parsedDate = format1.parse(date)
                    formattedDateString = format2.format(parsedDate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return formattedDateString
    }

    fun setDateAndTime(date: String, finalFormat: String?): String? {
        var formattedDateString: String? = null
        if (!TextUtils.isEmpty(date)) {
            val format1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format2 = SimpleDateFormat(finalFormat)
            if (!TextUtils.isEmpty(date) && !date.equals("-1", ignoreCase = true)) {
                try {
                    // format1.setTimeZone(TimeZone.getTimeZone("GMT0"));
                    val parsedDate = format1.parse(date)
                    formattedDateString = format2.format(parsedDate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return formattedDateString
    }

    fun getDateAndTime(date: String, finalFormat: String?): String? {
        var finalFormat = finalFormat
        var formattedDateString: String? = null
        if (!TextUtils.isEmpty(date)) {
            if (date.contains("T00:00:00")) {
                finalFormat = "MMMM dd, yyyy"
            }
            val format1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format2 = SimpleDateFormat(finalFormat)
            if (!TextUtils.isEmpty(date) && !date.equals("-1", ignoreCase = true)) {
                try {
//                 format1.setTimeZone(TimeZone.getTimeZone("GMT0"));
                    val parsedDate = format1.parse(date)
                    formattedDateString = format2.format(parsedDate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return formattedDateString
    }

    fun getDateAndTimeForMeasure(date: String, finalFormat: String?): String? {
        var finalFormat = finalFormat
        var formattedDateString: String? = null
        if (!TextUtils.isEmpty(date)) {
            if (date.contains("T00:00:00")) {
                finalFormat = "dd/MM/yyyy"
            }
            val format1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val format2 = SimpleDateFormat(finalFormat)
            if (!TextUtils.isEmpty(date) && !date.equals("-1", ignoreCase = true)) {
                try {
//                 format1.setTimeZone(TimeZone.getTimeZone("GMT0"));
                    val parsedDate = format1.parse(date)
                    formattedDateString = format2.format(parsedDate)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return formattedDateString
    }

    fun getMilliSecondFromString(date: String): Long {
        var timeInMilliseconds: Long = -1
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        if (!TextUtils.isEmpty(date) && !date.equals("-1", ignoreCase = true)) {
            try {
                val mDate = sdf.parse(date)
                timeInMilliseconds = mDate.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return timeInMilliseconds
    }

    fun getMilliSecondFromStringForDate(date: String): Long {
        var timeInMilliseconds: Long = -1
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        if (!TextUtils.isEmpty(date) && !date.equals("-1", ignoreCase = true)) {
            try {
                val mDate = sdf.parse(date)
                timeInMilliseconds = mDate.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return timeInMilliseconds
    }

    fun getMilliSecondFromStringForHrs(date: String, format: String?): Long {
        var timeInMilliseconds: Long = -1
        val sdf = SimpleDateFormat(format)
        if (!TextUtils.isEmpty(date) && !date.equals("-1", ignoreCase = true)) {
            try {
                sdf.timeZone = TimeZone.getTimeZone("GMT0")
                val mDate = sdf.parse(date)
                timeInMilliseconds = mDate.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return timeInMilliseconds
    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getMilliSeconds(date: String?, format: String?): Long {
        var timeInMilliseconds: Long = -1
        //        String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013";
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        try {
            val mDate = sdf.parse(date)
            timeInMilliseconds = mDate.time
            println("Date in milli :: $timeInMilliseconds")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeInMilliseconds
    }

    fun timeStringtoMilis(time: String?, format: String?): Long {
        var milis: Long = 0
        try {
            val sd = SimpleDateFormat(format, Locale.ENGLISH)
            val date = sd.parse(time)
            milis = date.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return milis
    }

    fun getDateFromMilliSeconds(milliSeconds: Long, dateFormat: String?): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getMilliSecondFromString(date: String, format: String?): Long {
        var timeInMilliseconds: Long = -1
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        if (!TextUtils.isEmpty(date) && !date.equals("-1", ignoreCase = true)) {
            try {
                val mDate = sdf.parse(date)
                timeInMilliseconds = mDate.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return timeInMilliseconds
    }

    val currentDate: String
        get() {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            return sdf.format(c.time)
        }

    val currentDateTime: String
        get() {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return sdf.format(c.time)
        }

    val currentDateTimeUTC: String
        get() {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(c.time)
        }

    // this takes current date
    val currentMonthFirstDate: String
        get() {
            val c = Calendar.getInstance() // this takes current date
            c[Calendar.DAY_OF_MONTH] = 1
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            return sdf.format(c.time)
        }

/* public static boolean isYesterday(long date) {
    Calendar now = Calendar.getInstance()
    Calendar cdate = Calendar.getInstance()
    cdate.setTimeInMillis(date)

    now.add(Calendar.DATE,-1)

    return now.get(Calendar.YEAR) == cdate.get(Calendar.YEAR)
    && now.get(Calendar.MONTH) == cdate.get(Calendar.MONTH)
    && now.get(Calendar.DATE) == cdate.get(Calendar.DATE)
}*/

    val inviteShowDate: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val calendar = Calendar.getInstance()
            //        calendar.setTime(myDate);
            calendar.add(Calendar.DAY_OF_YEAR, 6)
            return sdf.format(calendar.time)
        }
}