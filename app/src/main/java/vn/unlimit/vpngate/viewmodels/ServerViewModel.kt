package vn.unlimit.vpngate.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import vn.unlimit.vpngate.activities.paid.PaidServerActivity
import vn.unlimit.vpngate.api.ServerApiRequest
import vn.unlimit.vpngate.models.PaidServer
import vn.unlimit.vpngate.request.RequestListener
import java.lang.reflect.Type

class ServerViewModel(application: Application) : AndroidViewModel(application) {
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var serverList: MutableLiveData<ArrayList<PaidServer>> = MutableLiveData(ArrayList())
    private val serverApiRequest = ServerApiRequest()
    private var isOutOfData: Boolean = false

    companion object {
        const val ITEM_PER_PAGE = 30
        const val TAG = "ServerViewModel"
    }

    fun loadServer(activity: PaidServerActivity, loadFromStart: Boolean = false) {
        isLoading.value = true
        if (!isOutOfData || loadFromStart) {
            var skip = serverList.value?.size
            if (loadFromStart) {
                skip = 0
                isOutOfData = false
            }
            serverApiRequest.loadServer(ITEM_PER_PAGE, skip, object : RequestListener {
                override fun onSuccess(result: Any?) {
                    val type: Type = object : TypeToken<List<PaidServer?>?>() {}.type
                    val listServerArray = (result as JSONObject).get("listServer") as JSONArray
                    val listServer: ArrayList<PaidServer> =  Gson().fromJson(listServerArray.toString(), type)
                    if (loadFromStart) {
                        serverList.value = listServer
                    } else {
                        serverList.value?.addAll(listServer)
                    }
                    isOutOfData = serverList.value?.size!! >= result.get("countServer") as Int
                    isLoading.value = false
                }

                override fun onError(error: String?) {
                    isLoading.value = false
                    Log.e(TAG, "Load paid server error with message: %s".format(error))
                }
            }, activity)
        } else {
            isLoading.value = false
        }
    }
}