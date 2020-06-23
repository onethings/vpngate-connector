package vn.unlimit.vpngate.api

import android.util.Log
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import org.json.JSONObject
import vn.unlimit.vpngate.BuildConfig
import vn.unlimit.vpngate.request.RequestListener
import vn.unlimit.vpngate.utils.PaidServerUtil

class UserApiRequest : BaseApiRequest() {
    companion object {
        const val TAG = "UserApiRequest"
        const val USER_LOGIN_URL = "/user/login"
        const val GET_USER_URL = "/user/get"
        const val GET_CAPTCHA_URL = "/user/captcha"
        const val USER_REGISTER_URL = "/user/register"
    }

    fun login(username: String, password: String, requestListener: RequestListener) {
        val loginData = HashMap<String, String>()
        loginData["username"] = username
        loginData["password"] = password
        post(USER_LOGIN_URL, loginData, object : JSONObjectRequestListener {
            override fun onResponse(response: JSONObject?) {
                paidServerUtil.setStringSetting(PaidServerUtil.SESSION_ID_KEY, response!!.get("sessionId").toString())
                paidServerUtil.setStringSetting(PaidServerUtil.USER_INFO_KEY, response.get("user").toString())
                paidServerUtil.setIsLoggedIn(true)
                requestListener.onSuccess(response)
            }

            override fun onError(anError: ANError?) = requestListener.onError(anError!!.toString())
        })
    }

    fun fetchUser(requestListener: RequestListener) {
        get(GET_USER_URL, object : JSONObjectRequestListener {
            override fun onResponse(response: JSONObject?) {
                paidServerUtil.setStringSetting(PaidServerUtil.USER_INFO_KEY, response!!.toString())
                requestListener.onSuccess(response)
            }

            override fun onError(anError: ANError?) = errorHandle(anError, requestListener)
        })
    }

    fun getCaptcha(requestListener: RequestListener, width: Int? = 120, height: Int? = 90, fontSize: Int? = 60) {
        get("$GET_CAPTCHA_URL?width=$width&height=$height&fontSize=$fontSize", object : JSONObjectRequestListener {
            override fun onResponse(response: JSONObject?) {
                requestListener.onSuccess(response)
            }

            override fun onError(anError: ANError?) {
                Log.e(TAG, "Get captcha error: {}".format(anError!!.errorDetail.toString()))
                requestListener.onError(anError.errorDetail.toString())
            }
        })
    }

    fun register(username: String, fullName: String, email: String, password: String, birthDay: String, timeZone: String, repassword: String, captchaAnswer: Int, captchaSecret: String, requestListener: RequestListener) {
        val isPro = BuildConfig.FLAVOR == "pro"
        val user = HashMap<String, String>()
        user["username"] = username
        user["fullname"] = fullName
        user["email"] = email
        user["birthday"] = birthDay
        user["timezone"] = timeZone
        user["password"] = password
        user["repassword"] = repassword
        val captcha = HashMap<String, String>()
        captcha["answer"] = captchaAnswer.toString()
        captcha["secret"] = captchaSecret
        val data = HashMap<String, Any>()
        data["user"] = user
        data["captcha"] = captcha
        post("$USER_REGISTER_URL${if (isPro) "?version=pro" else ""}", data, object: JSONObjectRequestListener {
            override fun onResponse(response: JSONObject?) {
                Log.d(TAG, "Register success with email: {}".format(email))
                requestListener.onSuccess(response)
            }

            override fun onError(anError: ANError?) {
                Log.e(TAG, "Register error with detail: {}".format(anError!!.errorDetail))
                requestListener.onError(anError.response.body().toString())
            }
        })
    }
}