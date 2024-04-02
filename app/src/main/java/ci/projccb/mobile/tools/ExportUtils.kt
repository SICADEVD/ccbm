package ci.projccb.mobile.tools

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.repositories.apis.ApiClient
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLConnection


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 15/05/2022.
 **/
object ExportUtils {


    suspend fun expotToKml(fileName: String?, parcelle: ParcelleModel, action: Int, context: Context) {
        parcelle.mappingPoints = ApiClient.gson.fromJson(parcelle.wayPointsString, object : TypeToken<MutableList<String>>() {}.type)
        val myExternalFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName.plus(DateTime.now().toString(DateTimeFormat.forPattern("yyyyMMddHHmmss"))).plus(".kml").lowercase())
        val kmlBuilder = StringBuilder()
        kmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\">")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("    <Document>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("        <Placemark>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("            <name>${parcelle.producteurNom}</name>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("            <Style>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                 <LineStyle>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                     <color>ff0000ff</color>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                     <width>2</width>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                 </LineStyle>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                 <PolyStyle>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                     <outline>1</outline>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                     <fill>1</fill>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                     <color>5500FF00</color>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                 </PolyStyle>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("             </Style>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("            <description></description>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("            <Polygon>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                <outerBoundaryIs>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                    <LinearRing>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                        <coordinates>")
        kmlBuilder.append(System.getProperty("line.separator"))
        parcelle.mappingPoints.map {
            kmlBuilder.append("                            $it")
            kmlBuilder.append(System.getProperty("line.separator"))
        }
        kmlBuilder.append("                            ${parcelle.mappingPoints.first()}")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                        </coordinates>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                    </LinearRing>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("                </outerBoundaryIs>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("            </Polygon>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("        </Placemark>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("    </Document>")
        kmlBuilder.append(System.getProperty("line.separator"))
        kmlBuilder.append("</kml>")

        try {
            val fos = FileOutputStream(myExternalFile)
            fos.write(kmlBuilder.toString().toByteArray())
            fos.close()

            if (action == 1) {
                ShareCompat.IntentBuilder.from(context as AppCompatActivity)
                    .setStream(Uri.parse(myExternalFile.path))
                    .setType(URLConnection.guessContentTypeFromName(myExternalFile.name))
                    .startChooser();
            } else {
                try {
                    MainScope().launch {
                        Commons.showMessage(
                            "Fichier enregistré!",
                            context as AppCompatActivity,
                            finished = false,
                            positive = "OK",
                            callback = {}
                        )
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    suspend fun exportToGpx(fileName: String?, parcelle: ParcelleModel, action: Int, context: Context) {
        parcelle.mappingPoints = ApiClient.gson.fromJson(parcelle.wayPointsString, object : TypeToken<MutableList<String>>() {}.type)
        val myExternalFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName.plus(DateTime.now().toString(DateTimeFormat.forPattern("yyyyMMddHHmmss"))).plus(".gpx").lowercase())
        val gpxBuilder = StringBuilder()

        gpxBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("<gpx version=\"1.1\">")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("    <metadata>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("        <name>Mapping de parcelle</name>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("        <copyright author=\"CCB\"/>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("    </metadata>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("    <trk>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("        <name>${parcelle.producteurNom}</name>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("        <desc></desc>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("        <number>0</number>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("        <trkseg>")
        gpxBuilder.append(System.getProperty("line.separator"))

        parcelle.mappingPoints.map {
            gpxBuilder.append("        <trkpt lat=\"${it.split(",")[1]}\" lon=\"${it.split(",")[0]}\">")
            gpxBuilder.append(System.getProperty("line.separator"))
            gpxBuilder.append("            <time>2022-05-20T19:09:06Z</time>")
            gpxBuilder.append(System.getProperty("line.separator"))
            gpxBuilder.append("        </trkpt>")
            gpxBuilder.append(System.getProperty("line.separator"))
        }
        gpxBuilder.append("        <trkpt lat=\"${parcelle.mappingPoints.first().split(",")[1]}\" lon=\"${parcelle.mappingPoints.first().split(",")[0]}\">")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("            <time>2022-05-20T19:09:06Z</time>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("        </trkpt>")

        gpxBuilder.append(System.getProperty("line.separator"))

        gpxBuilder.append("        <trkpt lat=\"${parcelle.mappingPoints[1].split(",")[1]}\" lon=\"${parcelle.mappingPoints[1].split(",")[0]}\">")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("            <time>2022-05-20T19:09:06Z</time>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("        </trkpt>")

        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("    </trkseg>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("  </trk>")
        gpxBuilder.append(System.getProperty("line.separator"))
        gpxBuilder.append("</gpx>")
        gpxBuilder.append(System.getProperty("line.separator"))

        try {
            val fos = FileOutputStream(myExternalFile)
            fos.write(gpxBuilder.toString().toByteArray())
            fos.close()

            if (action == 1) {
                ShareCompat.IntentBuilder.from(context as AppCompatActivity)
                    .setStream(Uri.parse(myExternalFile.path))
                    .setType(URLConnection.guessContentTypeFromName(myExternalFile.name))
                    .startChooser();
            } else {
                try {
                    MainScope().launch {
                        Commons.showMessage(
                            "Fichier enregistré!",
                            context as AppCompatActivity,
                            finished = false,
                            positive = "OK",
                            callback = {}
                        )
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}
