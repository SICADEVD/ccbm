package ci.progbandama.mobile.tools

import android.os.AsyncTask
import com.blankj.utilcode.util.LogUtils
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class SendErrorOnline(private val formDataText: String) : AsyncTask<Void?, Void?, Void?>() {

    override fun doInBackground(vararg params: Void?): Void? {
        val url = URL("https://docs.google.com/forms/u/0/d/e/1FAIpQLSc978oeIocNbyNrJ-_nu0e9owEnnDZcyyqnFlmcmKTHZ_GA2g/formResponse")

        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true

            // Form data
            val formData = "entry.1142418849=${formDataText}"
            val postData = formData.toByteArray(StandardCharsets.UTF_8)

            // Set content length
            connection.setRequestProperty("Content-Length", postData.size.toString())

            // Write data
            val outputStream: OutputStream = BufferedOutputStream(connection.outputStream)
            val writer = OutputStreamWriter(outputStream, "UTF-8")
            writer.write(formData)
            writer.flush()
            writer.close()
            outputStream.close()

            // Get response code (optional)
            val responseCode = connection.responseCode
            println("Response Code: $responseCode")

            // Handle the response if needed

        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(e.message)
        }

        return null
    }
}

// To execute the task:
// SubmitFormTask().execute()
