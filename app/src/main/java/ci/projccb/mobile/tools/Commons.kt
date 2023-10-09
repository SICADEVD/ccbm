package ci.projccb.mobile.tools

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import ci.projccb.mobile.services.SynchronisationIntentService
import java.lang.Math.log10
import java.text.DecimalFormat
import kotlin.math.pow
import android.media.MediaPlayer
import android.os.Build
import android.text.InputFilter
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.DashboardAgentActivity
import ci.projccb.mobile.activities.forms.CalculEstimationActivity
import ci.projccb.mobile.activities.forms.FormationActivity
import ci.projccb.mobile.activities.forms.InspectionActivity
import ci.projccb.mobile.activities.forms.LivraisonActivity
import ci.projccb.mobile.activities.forms.ParcelleActivity
import ci.projccb.mobile.activities.forms.ProducteurActivity
import ci.projccb.mobile.activities.forms.ProducteurMenageActivity
import ci.projccb.mobile.activities.forms.SsrtClmsActivity
import ci.projccb.mobile.activities.forms.SuiviApplicationActivity
import ci.projccb.mobile.activities.forms.SuiviParcelleActivity
import ci.projccb.mobile.activities.forms.UniteAgricoleProducteurActivity
import ci.projccb.mobile.activities.lists.DatasDraftedListActivity
import ci.projccb.mobile.activities.lists.FormationsListActivity
import ci.projccb.mobile.activities.lists.LivraisonsListActivity
import ci.projccb.mobile.activities.lists.MenageresListActivity
import ci.projccb.mobile.activities.lists.ParcellesListActivity
import ci.projccb.mobile.activities.lists.ProducteursListActivity
import ci.projccb.mobile.activities.lists.SuiviPacellesListActivity
import ci.projccb.mobile.activities.lists.UpdateContentsListActivity
import ci.projccb.mobile.repositories.datas.CommonData
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.json.JSONException
import java.io.*
import java.math.RoundingMode


class Commons {


    companion object {
        const val TAG = "Commons.kt"
        const val blockCharacterSet = "~#^|,$%&*!0-+()/: "
        const val blockCharacterExcludeZeroSet = ";N~#^|,$%&*!-+()/:"
        var mpAudio: MediaPlayer? = null
        val CURRENCYLIB: String = "FCFA"


        val filterWithZero = InputFilter { source, start, end, dest, dstart, dend ->
            if (source != null && blockCharacterExcludeZeroSet.contains("" + source)) {
                ""
            } else {
                null
            }
        }


        fun applyFilters(component: AppCompatEditText?, withZero: Boolean = false) {
            component?.filters = arrayOf(filterWithZero)
        }

        fun applyFiltersDec(component: AppCompatEditText?, withZero: Boolean = false) {
            component?.filters = arrayOf<InputFilter>(DecimalInputFilter())
        }

        fun playDraftSound(ctx : Context) {
            mpAudio = MediaPlayer.create(ctx, R.raw.draft_effect)
            mpAudio?.start()
        }


        fun releaseDraftSound() {
            if (mpAudio != null) {
                if (mpAudio!!.isPlaying) {
                    mpAudio!!.stop()
                }
            }
        }


        fun showMessage(message: String, context: Context, finished: Boolean = false, callback: () -> Unit?, positive: String? = "Oui", deconnec: Boolean = false, showNo: Boolean = false) {
            try {
                val builder = AlertDialog.Builder(context)
                // Display a message on alert dialog
                builder.setMessage(message)
                builder.setCancelable(false)

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton(positive) { dialog, _ ->
                    dialog.dismiss()

                    if (callback != null) callback()

                    if (finished) {
                        //callback
                        (context as AppCompatActivity).finish()
                    }
                }

                if (showNo) {
                    builder.setNegativeButton("Non") { dialog, _ ->
                        dialog.dismiss()
                    }
                }

                // Finally, make the alert dialog using builder
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }


        fun synchronisation(type: String, context: Context) {
            val sync = Intent(context, SynchronisationIntentService::class.java)
            sync.putExtra("type", type)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.startForegroundService(sync)
            } else {
                context.startService(sync)
            }
        }

        fun loadJSONFromAsset(activity: Activity, assetFileName: String ): String? {
            try {
                // Read the JSON file from assets folder
                val inputStream: InputStream = activity.assets.open(assetFileName)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                val json = String(buffer)

                // Convert the JSON string to a JSON object
                return json

            } catch (e: JSONException) {
                e.printStackTrace()
                print("error Json "+e.message)
            } catch (e: IOException) {
                e.printStackTrace()
                print("error java "+e.message)
            }

            return null

        }

        fun encodeFileToBase64Binary(file: File?): String? {
            var encodedBase64: String? = null
            try {
                val fileInputStreamReader = FileInputStream(file)
                val bytes = ByteArray(file!!.length().toInt())
                fileInputStreamReader.read(bytes)
                encodedBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return encodedBase64
        }


        fun convertDate(date: String?, toEng: Boolean = false): String {
            val formatUnderscore = date?.split("-")

            val rightDigit = formatUnderscore?.map {
                if (it.length == 1) {
                    "0$it"
                } else {
                    it
                }
            }

            var reverseDate: List<String>? = null

            if (toEng) {
                reverseDate = rightDigit?.reversed()
            } else {
                reverseDate = rightDigit
            }

            val formatDate = reverseDate?.joinToString(
                separator = "-"
            )

            return formatDate!!
        }


        fun convertHour(hour: String) : String {
            return ""
        }


        fun convertBitmap2File(pBitmap: Bitmap?, pPath: String?) : String? {
            val file = File(pPath!!)
            val os: OutputStream = BufferedOutputStream(FileOutputStream(file))
            pBitmap?.compress(Bitmap.CompressFormat.PNG, 70, os)
            os.close()

            return file.absolutePath
        }


        fun convertPathBase64(filePath: String?, which: Int): String {
            if (filePath == null) return ""

            LogUtils.d(filePath)
            val imgFile = File(filePath)
            val options = BitmapFactory.Options()
            options.inSampleSize = 8
            val myBitmap = if (which == 3) BitmapFactory.decodeFile(imgFile.absolutePath) else BitmapFactory.decodeFile(imgFile.absolutePath, options)

            val byteArrayOutputStream = ByteArrayOutputStream()
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }


        fun convertDoubleToString(value: Double): String {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.DOWN
            return df.format(value)
        }


        private fun getReadableFileSize(size: Long): String {
            if (size <= 0) {
                return "0"
            }
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
        }


        fun loadFlipAnimation(context: Context): Animation {
            return AnimationUtils.loadAnimation(context, R.anim.flip)
        }


        fun loadShakeAnimation(context: Context): Animation {
            return AnimationUtils.loadAnimation(context, R.anim.shake)
        }


        fun provideDatasSpinnerSelection(spinner: Spinner?, value: String?, list: List<CommonData>) {
            for (i in 1..list.size) {
                if (value?.lowercase() == list[i - 1].nom?.lowercase()) {
                    spinner?.setSelection(i - 1)
                    break
                }
            }
        }


        fun provideStringSpinnerSelection(spinner: Spinner?, value: String?, list: Array<String>?) {
            for (i in 1..(list ?: arrayOf()).size) {
                if (value?.lowercase() == list?.get(i - 1)?.lowercase()) {
                    spinner?.setSelection(i - 1)
                    break
                }
            }
        }

        fun blockUpdateForFeature(fromMenu: String): Boolean {
            var isUpdated = true
            when (fromMenu.uppercase()) {
                "INSPECTION",
                "SSRTE",
                "LOCALITE",
                "INFOS_PRODUCTEUR",
                "MENAGE",
                "SUIVI_PARCELLE",
                "FORMATION",
                "CALCUL_ESTIMATION",
                "SUIVI_APPLICATION",
                "LIVRAISON" -> {
                    isUpdated = false
                }

            }

            return isUpdated
        }

        fun redirectMenu(fromMenu: String, actionMenu: String, activity: Activity) {
            when (actionMenu.uppercase()) {
                "ADD" -> {
                    when (fromMenu.uppercase()) {
                        "LOCALITE" -> Commons.showMessage("Cette fonctionnalité est désactivé", activity, finished = true, callback = {}, positive = "OKAY", deconnec = false, showNo = false) // ActivityUtils.startActivity(LocaliteActivity::class.java)
                        "PRODUCTEUR" -> ActivityUtils.startActivity(ProducteurActivity::class.java)
                        "INFOS_PRODUCTEUR" -> ActivityUtils.startActivity(
                            UniteAgricoleProducteurActivity::class.java)
                        "MENAGE" -> ActivityUtils.startActivity(ProducteurMenageActivity::class.java)
                        "PARCELLE" -> ActivityUtils.startActivity(ParcelleActivity::class.java)
                        "SUIVI_PARCELLE" -> ActivityUtils.startActivity(SuiviParcelleActivity::class.java)
                        "INSPECTION" -> ActivityUtils.startActivity(InspectionActivity::class.java)
                        "SSRTE" -> ActivityUtils.startActivity(SsrtClmsActivity::class.java)
                        "FORMATION" -> ActivityUtils.startActivity(FormationActivity::class.java)
                        "CALCUL_ESTIMATION" -> ActivityUtils.startActivity(CalculEstimationActivity::class.java)
                        "SUIVI_APPLICATION" -> ActivityUtils.startActivity(SuiviApplicationActivity::class.java)
                        "LIVRAISON" -> ActivityUtils.startActivity(LivraisonActivity::class.java)
                    }
                }

                "UPDATE" -> {
                    val intentUpdateContent = Intent(activity, UpdateContentsListActivity::class.java)
                    intentUpdateContent.putExtra("fromContent", fromMenu)
                    ActivityUtils.startActivity(intentUpdateContent)
                }

                "DRAFTS" -> {
                    val intentDraft = Intent(activity, DatasDraftedListActivity::class.java)
                    intentDraft.putExtra("fromMenu", fromMenu)
                    ActivityUtils.startActivity(intentDraft)
                }

                "DATAS" -> {
                    when (fromMenu.uppercase()) {
                        "LOCALITE" -> Commons.showMessage("Cette fonctionnalité est désactivé", activity, finished = true, callback = {}, positive = "OKAY", deconnec = false, showNo = false) //ActivityUtils.startActivity(LocalitesListActivity::class.java)
                        "PRODUCTEUR" -> ActivityUtils.startActivity(ProducteursListActivity::class.java)
                        //"INFOS_PRODUCTEUR" -> ActivityUtils.startActivity(UniteAgricoleProducteurActivity::class.java)
                        "MENAGE" -> ActivityUtils.startActivity(MenageresListActivity::class.java)
                        "PARCELLE" -> ActivityUtils.startActivity(ParcellesListActivity::class.java)
                        "SUIVI_PARCELLE" -> ActivityUtils.startActivity(SuiviPacellesListActivity::class.java)
                        //"INSPECTION" -> ActivityUtils.startActivity(EvaluationActivity::class.java)
                        //"SSRTE" -> ActivityUtils.startActivity(SsrtClmsActivity::class.java)
                        "FORMATION" -> ActivityUtils.startActivity(FormationsListActivity::class.java)
                        //"CALCUL_ESTIMATION" -> ActivityUtils.startActivity(CalculEstimationActivity::class.java)
                        //"SUIVI_APPLICATION" -> ActivityUtils.startActivity(SuiviApplicationActivity::class.java)
                        "LIVRAISON" -> ActivityUtils.startActivity(LivraisonsListActivity::class.java)
                    }
                }
            }
        }

        fun modifyIcColor(
            dashboardAgentActivity: Activity,
            imgProfileDashboard: AppCompatImageView,
            white: Int
        ) {
            val color = ContextCompat.getColor(dashboardAgentActivity, white)
            val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            imgProfileDashboard.colorFilter = colorFilter
        }

        fun modifyIcColor(
            dashboardAgentActivity: Context,
            imgProfileDashboard: ImageView,
            white: Int
        ) {
            val color = ContextCompat.getColor(dashboardAgentActivity, white)
            val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            imgProfileDashboard.colorFilter = colorFilter
        }

    }

}
