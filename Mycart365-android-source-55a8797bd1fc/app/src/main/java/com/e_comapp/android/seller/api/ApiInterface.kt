package com.e_comapp.android.seller.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface ApiInterface {
    @GET(URLs.APP_DEFAULTS)
    fun getAppDefaults(): Call<ResponseBody?>?

    @POST(URLs.SELLER_REG_LOGIN)
    fun postSellerReg(@Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.SELLER_OTP_VERIFY)
    fun postSellerOtpVerify(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @Multipart
    @POST(URLs.SELLER_UPDATE_DETAILS)
    fun postSellerDetails(@Header("usertype") userType: String?, @Part image: MultipartBody.Part?, @PartMap profileParams: Map<String, RequestBody>?): Call<ResponseBody?>

    @Multipart
    @POST(URLs.SELLER_ADD_PRODUCT)
    fun postSellerAddProduct(@Header("usertype") userType: String?, @Part uploadImage: Array<MultipartBody.Part?>?, @PartMap map: HashMap<String, RequestBody> /*,@Body JsonObject body*/): Call<ResponseBody?>?

    @POST(URLs.SELLER_PRODUCT_LIST)
    fun getSellerProductList(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.SELLER_DELETE_PRODUCT)
    fun postDeleteProduct(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>?

    @POST(URLs.OTP_VERIFTY)
    fun postCustOtpVerify(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.CUST_REG_LOGIN)
    fun postCustReg(@Body body: JsonObject?): Call<ResponseBody?>

    @Multipart
    @POST(URLs.CUST_UPDATE_DETAILS)
    fun postCustDetails(@Header("usertype") userType: String?, @Part image: MultipartBody.Part?, @PartMap profileParams: Map<String, RequestBody>?): Call<ResponseBody?>

    @Multipart
    @POST(URLs.CUST_UPDATE_DETAILS)
    fun postCustDetails(@Header("usertype") userType: String?, @PartMap profileParams: Map<String, RequestBody>?): Call<ResponseBody?>

    @POST(URLs.CUST_SELLERS)
    fun postGetCustSellers(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.CUST_CATEGORY_LIST)
    fun getCategoryListSeller(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.CUST_PRODUCT_LIST)
    fun postGetProdListSeller(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.CUST_SAVE_DELIVERY_ADDRESS)
    fun postSaveDeliveryAddress(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.CUST_GET_DELIVERY_ADDRESS)
    fun getDeliveryAddress(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.CUST_DELETE_DELIVERY_ADDRESS)
    fun deleteDeliveryAddress(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.CUST_ORDER_LIST)
    fun getCustOrderList(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>?

    @POST(URLs.CUST_CHECK_PRODUCT_QTY)
    fun postCheckProductQty(@Header("usertype") userType: String?, @Body entity: JsonObject?): Call<ResponseBody?>

    @POST(URLs.PLACE_ORDER)
    fun postPlaceOrder(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.COMPLETE_ORDER_TRANSACTION)
    fun postCompleteOrderTransaction(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>

    @POST(URLs.CUST_GET_PROMO_CODE)
    fun getPromoCode(@Header("usertype") userType: String?, @Body body: JsonObject?): Call<ResponseBody?>
}