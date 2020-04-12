package com.rstlss.app.planning

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rstlss.app.Async
import com.rstlss.app.R
import com.rstlss.app.noConnectionValue
import com.rstlss.app.weekdays
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class Planning {

    val programmes: ArrayList<Programme> = ArrayList()
    private var regularProgramme: String? = null
    val currentProgramme: MutableLiveData<String> = MutableLiveData()
    val isDataFetched: MutableLiveData<Boolean> = MutableLiveData()

    var timeZone: TimeZone = Calendar.getInstance().timeZone

    private fun findCurrentProgramme(): String
    {
        programmes.forEach {
            if (it.isCurrent())
                return it.title
        }
        return regularProgramme ?: noConnectionValue
    }

    fun checkProgramme()
    {
        val newProgramme = findCurrentProgramme()
        if (currentProgramme.value != newProgramme)
            currentProgramme.value = newProgramme
    }

    fun parseUrl(url: String? = null, context: Context? = null)
    {
        val scrape : (Any?) -> String = {
            if (url.isNullOrEmpty() && context != null)
            {
                val json: String
                try {
                    val inputStream = context.assets.open("planning_example.json")
                    val size = inputStream.available()
                    val buffer = ByteArray(size)
                    inputStream.use { it.read(buffer) }
                    json = String(buffer)
                    json
                } catch (ioException: IOException) {
                    ioException.printStackTrace()
                    ""
                }
            }
            else
                URL(url).readText()

        }
        val post : (parameter: Any?) -> Unit = {
            val result = JSONObject(it as String)
            if (result.has("planning"))
            {
                val programList = result.getJSONArray("planning")
                for (i in 0 until programList.length())
                {
                    val item = programList[i] as JSONObject
                    var periodicityDec = item.getInt("periodicity")
                    var periodicity = 0b0000000
                    var po = 1000000
                    for (j in 0 until weekdays.size)
                    {
                        if (periodicityDec / (po) > 0)
                        {
                            periodicityDec -= po
                            periodicity += 1 shl (weekdays.size-1 - j)
                        }
                        po /= 10
                    }
                    val hourBeginS = item.getString("hour_begin").split(":")
                    val hourBegin = hourBeginS.first().toInt()*60 + hourBeginS.last().toInt()
                    val hourEndS = item.getString("hour_end").split(":")
                    val hourEnd = hourEndS.first().toInt()* 60 + hourEndS.last().toInt()
                    val title = item.getString("title")
                    programmes.add(Programme(title, periodicity, hourBegin, hourEnd))
                }
            }
            regularProgramme = if (result.has("regular_programme"))
                result.getString("regular_programme")
            else
                context?.getString(R.string.regular_programme)

            timeZone = if (result.has("timezone"))
                TimeZone.getTimeZone(result.getString("timezone"))
            else
                Calendar.getInstance().timeZone

            isDataFetched.value = true
        }
        com.rstlss.app.Async(scrape, post)
    }

    companion object {
        val instance by lazy {
            Planning()
        }
    }
}