package ci.progbandama.mobile.tools

import androidx.room.TypeConverter
import ci.progbandama.mobile.repositories.apis.ApiClient
import com.google.gson.reflect.TypeToken

object ListConverters {


    @TypeConverter
    fun mutableListToString(datas: MutableList<String>?): String? {
        return ApiClient.gson.toJson(datas)
    }


    fun stringToMutableList(datas: String?) : MutableList<String>? {
        val arrayStringType = object : TypeToken<MutableList<String>>() {}.type
        return ApiClient.gson.fromJson(datas, arrayStringType)
    }

}
