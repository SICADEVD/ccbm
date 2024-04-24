package ci.projccb.mobile.tools

//import kotlinx.android.synthetic.main.activity_suivi_application.clickSaveMatiereSuiviApplication

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.InputFilter
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ci.projccb.mobile.R
import ci.projccb.mobile.activities.forms.CalculEstimationActivity
import ci.projccb.mobile.activities.forms.DistributionArbreActivity
import ci.projccb.mobile.activities.forms.EvaluationArbreActivity
import ci.projccb.mobile.activities.forms.FormationActivity
import ci.projccb.mobile.activities.forms.InspectionActivity
import ci.projccb.mobile.activities.forms.LivraisonActivity
import ci.projccb.mobile.activities.forms.LivraisonCentralActivity
import ci.projccb.mobile.activities.forms.ParcelleActivity
import ci.projccb.mobile.activities.forms.PostPlantingEvalActivity
import ci.projccb.mobile.activities.forms.ProducteurActivity
import ci.projccb.mobile.activities.forms.ProducteurMenageActivity
import ci.projccb.mobile.activities.forms.SsrtClmsActivity
import ci.projccb.mobile.activities.forms.SuiviApplicationActivity
import ci.projccb.mobile.activities.forms.SuiviParcelleActivity
import ci.projccb.mobile.activities.forms.UniteAgricoleProducteurActivity
import ci.projccb.mobile.activities.forms.VisiteurFormationActivity
import ci.projccb.mobile.activities.forms.views.MultiSelectSpinner
import ci.projccb.mobile.activities.lists.DatasDraftedListActivity
import ci.projccb.mobile.activities.lists.DatasSyncListActivity
import ci.projccb.mobile.activities.lists.FormationsListActivity
import ci.projccb.mobile.activities.lists.LivraisonsListActivity
import ci.projccb.mobile.activities.lists.MenageresListActivity
import ci.projccb.mobile.activities.lists.ParcellesListActivity
import ci.projccb.mobile.activities.lists.ProducteursListActivity
import ci.projccb.mobile.activities.lists.SuiviPacellesListActivity
import ci.projccb.mobile.activities.lists.UpdateContentsListActivity
import ci.projccb.mobile.adapters.MultipleItemAdapter
import ci.projccb.mobile.adapters.NineItemAdapter
import ci.projccb.mobile.adapters.OmbrageAdapter
import ci.projccb.mobile.adapters.OnlyFieldAdapter
import ci.projccb.mobile.adapters.SixItemAdapter
import ci.projccb.mobile.models.AdapterItemModel
import ci.projccb.mobile.models.OmbrageVarieteModel
import ci.projccb.mobile.models.ParcelleModel
import ci.projccb.mobile.repositories.datas.CommonData
import ci.projccb.mobile.services.SynchronisationIntentService
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.reflect.TypeToken
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarLayout
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import com.toptoche.searchablespinnerlibrary.SearchableSpinner

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.json.JSONException
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Math.log10
import java.math.RoundingMode
import java.net.UnknownHostException
import java.text.DecimalFormat
import java.util.Calendar
import java.util.Objects
import kotlin.math.pow
import kotlin.math.roundToInt


interface LoadProgressListener {
    fun startLoadProgress(content: String = "")
}

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

        fun addItemsToList(keyI: String, valueI: String, livraisonItemsListPrev: MutableList<Map<String, String>> ) {
            val item = mutableMapOf<String,String>()
            item.put(keyI, valueI)
            livraisonItemsListPrev.add(item)
        }

//        fun setListenerForSpinner(context:Context, spinner:AppCompatSpinner, listIem: List<String?> = mutableListOf(), itemChanged:String? = null, currentVal:String? = null, onChanged:((value:String) -> Unit), onSelected:((value:Int) -> Unit)){
//
//            if(listIem.size > 0) spinner.adapter = ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, listIem)
//            spinner.onItemSelectedListener = object : OnItemSelectedListener{
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                    val selectedItem: String = p0?.getItemAtPosition(p2).toString()
//                    selectedItem.let {
//                        if(it.equals(itemChanged, ignoreCase = true)) onSelected.invoke(View.VISIBLE)
//                        else  onSelected.invoke(View.GONE)
//                    }
//                    onChanged.invoke(selectedItem)
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//
//                }
//            }
//            currentVal?.let {
//                var curr = 0
//                for (item in listIem){
//                    if (it.equals(item)) spinner.setSelection(curr)
//                    curr++
//                }
//            }
//        }
        fun setOnlyOneITemSApplicRV(ctx: Context, recycler:RecyclerView, button:Button, editT: EditText, libeleList:MutableList<String> = arrayListOf(), clickItem: () -> Unit?) {
            val itemList = mutableListOf<CommonData>()
            var countN = 0
            libeleList.forEach {
                itemList.add(CommonData(0, it))
                countN++
            }

            val itemAdapter = OnlyFieldAdapter(itemList)
            try {
                recycler.layoutManager =
                    LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
                recycler.adapter = itemAdapter
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

            button.setOnClickListener {
                try {
                    if (editT.text.toString()
                            .isEmpty()
                    ) {
                        Commons.showMessage("Renseignez des données sur l'animal, svp !", ctx, callback = {})
                        return@setOnClickListener
                    }

                    val item = CommonData(
                        0,
                        editT.text.toString().trim(),
                    )

                    if(item.nom?.length?:0 > 0){
                        itemList?.forEach {
                            if (it.nom?.uppercase() == item.nom?.uppercase()) {
                                ToastUtils.showShort("Cet élément est déja ajouté")

                                return@setOnClickListener
                            }
                        }

                        itemList?.add(item)
                        itemAdapter?.notifyDataSetChanged()

                        editT.text?.clear()
                    }

                    clickItem.invoke()

                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

        }


        fun setListenerForSpinner(context:Context, title:String = "Faite un choix !",
                                  message: String = "La liste est vide !", isKill:Boolean = false,
                                  isEmpty:Boolean = false, spinner: Spinner,
                                  listIem: List<String?> = mutableListOf(), itemChanged:List<Pair<Int,String>>? = null,
                                  currentVal:String? = null, onChanged:((value:Int) -> Unit),
                                  onSelected:((itemId:Int,visibility:Int) -> Unit)){

            if(spinner == null) return

            if(spinner is SearchableSpinner) {
                (spinner as SearchableSpinner).setTitle(title)
                (spinner as SearchableSpinner).setPositiveButton("Fermer !")
            }

            var listIemCur = mutableListOf<String>()
            listIemCur.add("Faites un choix")
            listIem?.forEach {
                listIemCur.add(it.toString())
            }

            if(listIemCur.size > 0) {
                spinner.adapter =
                    ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, listIemCur)
            }else{
                if(isEmpty){
                    spinner.adapter =
                        ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, listIemCur)
//                    LogUtils.d(listIem)
                    MainScope().launch {
                        showMessage(
                            message,
                            context,
                            finished = isKill,
                            callback = {},
                            positive = "Compris !",
                            deconnec = false,
                            showNo = false
                        )
                    }
                }
            }
            spinner.onItemSelectedListener = object : OnItemSelectedListener{
                override fun onItemSelected(p0:  AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    //LogUtils.d("POSITION ", p2)
                    if (p2 == 0) {
                        return
                    }
                    val selectedItem: String = p0?.getItemAtPosition(p2).toString()
                    selectedItem.let {
                        itemChanged?.let { changedText ->
                            if(changedText.size == 1){
                                if(changedText.get(0).second.contains(it, ignoreCase = true)) {
                                    onSelected.invoke(changedText.get(0).first, View.VISIBLE)
                                }else onSelected.invoke(changedText.get(0).first, View.GONE)
                            }else{
                                var isFind = false
                                for (item in changedText){
                                    if(item.second.equals(it, ignoreCase = true)) {
                                        onSelected.invoke(item.first, View.VISIBLE)
                                        isFind = true
                                    }
                                }
                                if(isFind == false) onSelected.invoke(0, View.GONE)
                            }

                        }
                    }
                    onChanged.invoke(p2-1)

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
            currentVal?.let {
                var curr = 0
                for (item in listIemCur){
                    if (it.equals(item, ignoreCase = true)) spinner.setSelection(curr)
                    curr++
                }
            }
        }

        fun calculateTotalHeight(context:Context, recyclerView: RecyclerView, childHeiht: Int = 50): Int {
            val layoutManager = recyclerView.layoutManager
            val pixels: Int = (childHeiht * context.resources.displayMetrics.density).toInt()
            var totalHeight = pixels*layoutManager!!.itemCount

            LogUtils.d(totalHeight)

            return totalHeight
        }

        fun <T : Any>  getAllTitleAndValueViews(
            viewGroup: ViewGroup,
            prodModel: T,
            isTakeHide: Boolean = false,
            mutableListOf: MutableList<Pair<String, String>>,
            currTextView:String? = null
        ): T {
            val childCount = viewGroup.childCount
            var canModifTitle = true
            var currTextViewIn = currTextView
            for (i in 0 until childCount) {
                val childView = viewGroup.getChildAt(i)

                val childViewClassName = childView::class.java.simpleName
                //LogUtils.d(childViewClassName)
                if (childViewClassName.contains("AppCompatTextView", ignoreCase = true) && childView.tag == null) {
                    currTextViewIn = (childView as TextView).text.toString()
                    //LogUtils.d(childViewClassName+ " "+ currTextView)
                }

                if ( (childView is Spinner) && childView.tag != null) {
                    var value = ""
                    childView.selectedItem?.let {
                        value = it as String
                    }
                    //LogUtils.d("Spinner ${value} "+ childView::class.java.simpleName)
                    val producteurModelClass = prodModel.javaClass
                    val memberProperty = producteurModelClass.declaredFields.find { it.name == childView.tag }
                    memberProperty?.let {
                        it.isAccessible = true
                        it.set(prodModel, value.toCheckEmptyItem().returnIfFindEmpty())
//                        LogUtils.d(value)
                        mutableListOf.add(Pair(currTextViewIn.toString(), value))
                    }
                } else if ( childView is AppCompatEditText && childView.tag != null ) {
                    // You've found an EditText with the specified tag, get its value
                    val editText = childView as AppCompatEditText
                    val value = editText.text.toString()
                    //LogUtils.d("AppCompatEditText ${value} "+ childView::class.java.simpleName)
//                    val paired = mutableListOf.get(countField)


                    // Use reflection to set the property value dynamically
                    val producteurModelClass = prodModel.javaClass
                    val memberProperty = producteurModelClass.declaredFields.find { it.name == editText.tag }
                    memberProperty?.let {
                        it.isAccessible = true
                        it.set(prodModel, value.toCheckEmptyItem().returnIfFindEmpty())
                        mutableListOf.add(Pair(currTextViewIn.toString(), value))
                    }
                    //countField++
                } else if (childView is ViewGroup) {
                    // If it's a ViewGroup, recursively call this method
                    if(childView.visibility == View.VISIBLE && isTakeHide == false)
                    {
                        getAllTitleAndValueViews(
                            viewGroup = childView,
                            prodModel = prodModel,
                            mutableListOf = mutableListOf,
                            currTextView = currTextViewIn
                        )
                    }
                }
            }
            return prodModel
        }

        fun String.returnIfFindEmpty(): String? {
            if(this == null){
                return null
            }else if(this.equals("0"))
                return null

            return this
        }

        fun setSizeOfAllTextViews(
            context: Context,
            viewGroup: ViewGroup,
            textSize:Float? = null,
            editTextSize:Float? = null
        ) {
            val childCount = viewGroup.childCount

            for (i in 0 until childCount) {
                val childView = viewGroup.getChildAt(i)

                val childViewClassName = childView::class.java.simpleName
                //LogUtils.d(childViewClassName)
                if (childViewClassName.contains("AppCompatTextView", ignoreCase = true) && childView.tag == null) {
                    (childView as TextView).textSize = textSize?:context.resources.getDimension(R.dimen._8ssp)
                    //LogUtils.d(childViewClassName+ " "+ currTextView)
                }

                if ( (childView is Spinner) && childView.tag != null) {

                } else if ( childView is AppCompatEditText && childView.tag != null ) {
                    // You've found an EditText with the specified tag, get its value
                    childView.textSize = editTextSize?:context.resources.getDimension(R.dimen._8ssp)
                } else if (childView is ViewGroup) {
                    // If it's a ViewGroup, recursively call this method
                    setSizeOfAllTextViews(
                        context = context,
                        viewGroup = childView,
                        textSize = textSize,
                        editTextSize = editTextSize
                    )
                }
            }
        }

        fun setListenerForViewsChange(
            context: Context,
            viewGroup: ViewGroup,
            loadProgressListener: LoadProgressListener
        ) {
            val childCount = viewGroup.childCount

            for (i in 0 until childCount) {
                val childView = viewGroup.getChildAt(i)

                val childViewClassName = childView::class.java.simpleName

                if ( (childView is Spinner) && childView.tag != null) {

                    val listener = childView.onItemSelectedListener
                    (childView as Spinner).onItemSelectedListener = object : OnItemSelectedListener{
                        override fun onItemSelected(p0:  AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            loadProgressListener.startLoadProgress("")
                            listener?.onItemSelected(p0, p1, p2, p3)
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }
                    }

                } else if ( childView is AppCompatEditText) {

                    childView.setOnTouchListener(object : View.OnTouchListener {
                        override fun onTouch(v: View?, event: MotionEvent): Boolean {
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                loadProgressListener.startLoadProgress("")
                                return false
                            }
                            return false
                        }
                    })

                } else if (childView is ViewGroup) {

                    setListenerForViewsChange(
                        context = context,
                        viewGroup = childView,
                        loadProgressListener
                    )
                }
            }
        }

        fun <T : Any>  setAllValueOfTextViews(
            viewGroup: ViewGroup,
            prodModel: T,
            makeDisable: Boolean = false,
            ignoreDisable: MutableList<String> = mutableListOf<String>()
        ): T {
            val childCount = viewGroup.childCount
            for (i in 0 until childCount) {
                val childView = viewGroup.getChildAt(i)

                val childViewClassName = childView::class.java.simpleName
                //LogUtils.d(childViewClassName)
                if (childViewClassName.contains("AppCompatTextView", ignoreCase = true) && childView.tag == null) {
                    //currTextViewIn = (childView as TextView).text.toString()
                    //LogUtils.d(childViewClassName+ " "+ currTextView)
                }

                if ( (childView is Spinner) && childView.tag != null) {

                    if(makeDisable && ignoreDisable.contains(childView.tag) == false) childView.isEnabled = false

                }else if ( childView is AppCompatEditText && childView.tag != null ) {
                    // You've found an EditText with the specified tag, get its value
                    val editText = childView as AppCompatEditText

                    if(makeDisable && ignoreDisable.contains(editText.tag) == false) editText.isEnabled = false

                    val producteurModelClass = prodModel.javaClass
                    val memberProperty = producteurModelClass.declaredFields.find { it.name == editText.tag }
                    memberProperty?.let {
                        it.isAccessible = true
                        val propertyValue = it.get(prodModel)
                        if(propertyValue != null){
                            if(propertyValue.toString().lowercase() != "null") editText.setText("$propertyValue") else editText.setText("")
                        }else editText.setText("")
                    }
                    //countField++
                } else if (childView is ViewGroup) {
                    // If it's a ViewGroup, recursively call this method
                    setAllValueOfTextViews(childView, prodModel, makeDisable, ignoreDisable)
                }
            }
            return prodModel
        }

        fun releaseDraftSound() {
            if (mpAudio != null) {
                if (mpAudio!!.isPlaying) {
                    mpAudio!!.stop()
                }
            }
        }


        fun showMessage(message: String, context: Context, finished: Boolean = false, callback: () -> Unit?, positive: String? = "Oui", deconnec: Boolean = false, showNo: Boolean = false, textSizeDim: Int = R.dimen._8ssp) {
            try {
                val builder = AlertDialog.Builder(context, R.style.DialogTheme)
                // Display a message on alert dialog
                Commons.adjustTextViewSizesInDialog(context, builder, message, context.resources.getDimension(textSizeDim)
                    ,false)
                //builder.setMessage(message)
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
                    builder.setNegativeButton(context.getString(R.string.non)) { dialog, _ ->
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

        fun List<String>?.toModifString(isComma:Boolean = true, commaReplace:String = "", prefix:String = ""): String {
            val list = this?.map {
                var dani = "${it}"
                if(prefix.isNotEmpty()) dani = "${prefix} ${it}"
                dani
            }
            val values = list.toString().replace("]", "").replace("[", "")
            return if(isComma) values.replace(", ", commaReplace) else values
        }

        fun List<String>?.limitListByCount(limit:Int = 0): List<String>? {
            var list = mutableListOf<Int>()
            for (i in 0 until limit){
                list.add(i)
            }
            return this?.filterIndexed { index, s -> list.contains(index) == true }?.toList()
        }

        fun String.toUtilInt(): Int? {
            if( (this as String).isNullOrEmpty() ) return null
            return (this as String).toInt()
        }
        fun String?.toCheckEmptyItem(): String {
            if(listOf<String>("Choisir la note", "Faites un choix").contains(this)) return ""
            return (this as String)
        }
        fun returnStringList(value: String?): MutableList<String>? {
            if(value != null){
               //value = null
                return GsonUtils.fromJson<MutableList<String>>(value, object: TypeToken<MutableList<String>>(){}.type)
            }
            return mutableListOf()
        }

        fun Spinner.isSpinnerEmpty(): Boolean {
            return (this as Spinner).selectedItem.toString().isNullOrBlank()
        }

        fun Spinner.getSpinnerContent(): String {
            return (this as Spinner).selectedItem.toString().trim()
        }

        fun Context.showYearPickerDialog(editText: EditText) {
            val builder = AlertDialog.Builder(this)
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.year_picker_dialog, null)
            val yearPicker = dialogView.findViewById<NumberPicker>(R.id.yearPicker)

            // Change this to your desired initial year
            yearPicker.minValue = (year - 100) // Set the min year as needed
            yearPicker.maxValue = year // Set the max year as needed
            yearPicker.value = if(editText.text.isNullOrEmpty()) year else editText.text.toString().toIntOrNull()?:0

            builder.setView(dialogView)
            //builder.setTitle("Choix de l'année")
            Commons.adjustTextViewSizesInDialog(this, builder, "Choix de l'année",   this.resources.getDimension(R.dimen._6ssp)
            ,true)
            builder.setPositiveButton("Valider !") { _, _ ->
                val selectedYear = yearPicker.value
                editText.setText(selectedYear.toString())
                // Handle the selected year
            }
            builder.setNegativeButton("Annuler") { _, _ ->
                // Handle cancel
            }

            val dialog = builder.create()

            dialog.show()
        }

        fun String.formatCorrectlyLatLongPoint(): String {
            /*if (this != null){
                val indexOfDot = this.indexOf('.') // Find the index of the dot
                val sixCharsAfterDot = this.substring(0, indexOfDot + 7) // Extract substring with 6 characters after dot
                return sixCharsAfterDot
            }*/

            return this
        }

        fun Context.configDate(viewClciked: AppCompatEditText, isDateMin: Boolean = false, isDateMax: Boolean = true) {
            val calendar: Calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(this, { p0, year, month, day ->
                viewClciked.setText(convertDate("${day}-${(month + 1)}-$year", false))
            }, year, month, dayOfMonth)

            if(isDateMax) datePickerDialog.datePicker.maxDate = DateTime.now().millis
            if(isDateMin) datePickerDialog.datePicker.minDate = DateTime.now().millis
            datePickerDialog.show()
        }

        fun Context.configHour(viewClciked: AppCompatEditText) {
            // Get Current Time
            val c: Calendar = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this, { timePickerView, hourOfDay, minute -> viewClciked.setText("$hourOfDay:$minute") },
                mHour,
                mMinute,
                true
            )
            timePickerDialog.show()
        }

        fun adjustTextViewSizesInDialog(context: Context, dialogBuilder: Any, title: String, textSize: Float, isTitle:Boolean = true, isprogress:Boolean = false) {
            if (dialogBuilder == null) {
                return  // Handle null dialog case
            }
            val className = dialogBuilder::class.java.simpleName
            val inflater = LayoutInflater.from(context)
            var customTitleView: View = inflater.inflate(ci.projccb.mobile.R.layout.custom_title_layout, null)
            if(className.contains("ProgressDialog")) customTitleView = inflater.inflate(ci.projccb.mobile.R.layout.custom_progress_dial, null)
            if(isprogress) customTitleView = inflater.inflate(ci.projccb.mobile.R.layout.custom_progress_dial, null)

            val titleTextView = customTitleView.findViewById<View>(ci.projccb.mobile.R.id.title_text_view) as AppCompatTextView
//            LogUtils.d(className)
//            val parentGrp: ViewGroup? = titleTextView.getParent() as (ViewGroup)
//            parentGrp?.let {
//                parentGrp?.removeView(titleTextView)
//            }

            titleTextView.text = title.uppercase()
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            if(isTitle){
                if(className.contains("ProgressDialog") == false){
                    if(dialogBuilder is androidx.appcompat.app.AlertDialog.Builder){
                        (dialogBuilder as androidx.appcompat.app.AlertDialog.Builder).setCustomTitle(customTitleView)
                    }else{
                        (dialogBuilder as AlertDialog.Builder).setCustomTitle(customTitleView)
                    }
                }else{
                    (dialogBuilder as ProgressDialog).setCustomTitle(customTitleView)
                }
            }else{
                if(className.contains("ProgressDialog") == false){
                    if(dialogBuilder is androidx.appcompat.app.AlertDialog.Builder){
                        (dialogBuilder as androidx.appcompat.app.AlertDialog.Builder).setCustomTitle(customTitleView)
                    }else{
                        (dialogBuilder as AlertDialog.Builder).setView(customTitleView)
                    }
                }else{
                    (dialogBuilder as ProgressDialog).setContentView(customTitleView)
                }

            }
        }


        fun Context.limitEDTMaxLength(editText: EditText, minLength:Int = 225, maxLength:Int = 225){
            val filterArray = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength),
                                                    InputFilter.LengthFilter(minLength))
            var context = this
            editText.filters = filterArray

            editText.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                } else {
                    val textFiedl = (view as EditText)
                    if (textFiedl.text.trim().toString().length < minLength) {
                        MainScope().launch{
                            showMessage(
                                "Le contenu est inférieur à ${minLength}",
                                context,
                                finished = false,
                                {},
                                "Compris !",
                                deconnec = false,
                                showNo = false
                            )
                        }
                    }

                    if (textFiedl.text.trim().toString().length > maxLength) {
                        MainScope().launch{
                            showMessage(
                                "Le contenu est supérieur à ${maxLength}",
                                context,
                                finished = false,
                                {},
                                "Compris !",
                                deconnec = false,
                                showNo = false
                            )
                        }
                    }
                }
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

        fun fileToBase64(filePath: String?): String? {
            if (filePath.isNullOrEmpty()) return ""
            val fileBytes: ByteArray = FileUtils.getFileByPath(filePath).readBytes()
            return Base64.encodeToString(fileBytes, Base64.DEFAULT)
        }

        fun convertPathBase64(filePath: String?, which: Int): String {
            if (filePath.isNullOrEmpty()) return ""

            LogUtils.d(filePath)
            val imgFile = File(filePath)
            val options = BitmapFactory.Options()
            options.inSampleSize = 8
            var myBitmap = if (which == 3) BitmapFactory.decodeFile(imgFile.absolutePath) else BitmapFactory.decodeFile(imgFile.absolutePath, options)
            myBitmap = if (which == 0) BitmapFactory.decodeFile(imgFile.absolutePath) else BitmapFactory.decodeFile(imgFile.absolutePath, options)

            val byteArrayOutputStream = ByteArrayOutputStream()
            try{
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            }catch (ex:Exception){
                LogUtils.e(ex)
            }
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
                "SSRTECLMRS",
                "LOCALITE",
                "INFOS_PRODUCTEUR",
                "MENAGE",
                "PARCELLES",
                "FORMATION",
                "ESTIMATION",
                "APPLICATION",
                "LIVRAISON" -> {
                    isUpdated = false
                }

            }

            return isUpdated
        }

        fun redirectMenu(fromMenu: String, actionMenu: String, activity: Activity) {
//            MainScope().launch {
//                checkNetworkAvailablility()
//            }

            CoroutineScope(Dispatchers.IO).launch {
                var networkFlag = false
                try {
                    networkFlag = NetworkUtils.isAvailable()
                } catch (ex: UnknownHostException) {
                    networkFlag = false
                    LogUtils.e("Internet error !")
                }

                if (networkFlag) {
                    MainScope().launch {
                        Commons.synchronisation("all",  activity)
                    }
                }
            }

            when (actionMenu.uppercase()) {
                "ADD" -> {
                    when (fromMenu.uppercase()) {
                        "LOCALITE" -> Commons.showMessage("Cette fonctionnalité est désactivé", activity, finished = true, callback = {}, positive = "Compris !", deconnec = false, showNo = false) // ActivityUtils.startActivity(LocaliteActivity::class.java)
                        "PRODUCTEUR" -> ActivityUtils.startActivity(ProducteurActivity::class.java)
                        "INFOS_PRODUCTEUR" -> ActivityUtils.startActivity(
                            UniteAgricoleProducteurActivity::class.java)
                        "MENAGE" -> ActivityUtils.startActivity(ProducteurMenageActivity::class.java)
                        "PARCELLE" -> ActivityUtils.startActivity(ParcelleActivity::class.java)
                        "PARCELLES" -> ActivityUtils.startActivity(SuiviParcelleActivity::class.java)
                        "INSPECTION" -> ActivityUtils.startActivity(InspectionActivity::class.java)
                        "SSRTECLMRS" -> ActivityUtils.startActivity(SsrtClmsActivity::class.java)
                        "FORMATION" -> ActivityUtils.startActivity(FormationActivity::class.java)
                        "ESTIMATION" -> ActivityUtils.startActivity(CalculEstimationActivity::class.java)
                        "APPLICATION" -> ActivityUtils.startActivity(SuiviApplicationActivity::class.java)
                        "LIVRAISON" -> ActivityUtils.startActivity(LivraisonActivity::class.java)
                        "AGRO_DISTRIBUTION" -> ActivityUtils.startActivity(DistributionArbreActivity::class.java)
                        "POSTPLANTING" -> ActivityUtils.startActivity(PostPlantingEvalActivity::class.java)
                        "AGRO_EVALUATION" -> ActivityUtils.startActivity(EvaluationArbreActivity::class.java)
                        "FORMATION_VISITEUR" -> ActivityUtils.startActivity(VisiteurFormationActivity::class.java)
                        "LIVRAISON_MAGCENTRAL" -> ActivityUtils.startActivity(LivraisonCentralActivity::class.java)
                    }
                }

                "UPDATE" -> {
                    val intentUpdateContent = Intent(activity, UpdateContentsListActivity::class.java)
                    intentUpdateContent.putExtra("fromContent", fromMenu)
                    ActivityUtils.startActivity(intentUpdateContent)
                }

                "SYNC_UPDATE" -> {
                    val intentUpdateContent = Intent(activity, DatasSyncListActivity::class.java)
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
                        "LOCALITE" -> Commons.showMessage("Cette fonctionnalité est désactivé", activity, finished = true, callback = {}, positive = "Compris !", deconnec = false, showNo = false) //ActivityUtils.startActivity(LocalitesListActivity::class.java)
                        "PRODUCTEUR" -> ActivityUtils.startActivity(ProducteursListActivity::class.java)
                        //"INFOS_PRODUCTEUR" -> ActivityUtils.startActivity(UniteAgricoleProducteurActivity::class.java)
                        "MENAGE" -> ActivityUtils.startActivity(MenageresListActivity::class.java)
                        "PARCELLE" -> ActivityUtils.startActivity(ParcellesListActivity::class.java)
                        "PARCELLES" -> ActivityUtils.startActivity(SuiviPacellesListActivity::class.java)
                        //"INSPECTION" -> ActivityUtils.startActivity(EvaluationActivity::class.java)
                        //"SSRTE" -> ActivityUtils.startActivity(SsrtClmsActivity::class.java)
                        "FORMATION" -> ActivityUtils.startActivity(FormationsListActivity::class.java)
                        //"ESTIMATION" -> ActivityUtils.startActivity(CalculEstimationActivity::class.java)
                        //"APPLICATION" -> ActivityUtils.startActivity(SuiviApplicationActivity::class.java)
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

        fun setEditAndSpinnerRV(
            context: Activity,
            recyclerList: RecyclerView,
            addBtn: AppCompatButton,
            editItem: AppCompatEditText,
            selectItem: AppCompatSpinner,
            libelle: String = "Libellé",
            valeur: String = "Valeur",
            libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf(), isInverted:Boolean = false ) {
            val itemList = mutableListOf<OmbrageVarieteModel>()
            var countN = 0
            libeleList.forEach {
                if(isInverted){
                    itemList.add(OmbrageVarieteModel(0, valueList.get(countN), it))
                }else itemList.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
                countN++
            }
            val itemAdapter = OmbrageAdapter(itemList, libelle, valeur)
            try {
                recyclerList.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerList.adapter = itemAdapter
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

            addBtn.setOnClickListener {
                try {
                    if (editItem.text.toString()
                            .isEmpty() || selectItem.isSpinnerEmpty()
                    ) {
                        Commons.showMessage("Renseignez des données sur l'autre insecte, svp !", context, callback = {})
                        return@setOnClickListener
                    }

                    var modelAdd: OmbrageVarieteModel? = null
                    if(isInverted){
                        modelAdd = OmbrageVarieteModel(
                            0,
                            selectItem.selectedItem.toString().trim(),
                            editItem.text.toString().trim()
                        )
                    }else{
                        modelAdd = OmbrageVarieteModel(
                            0,
                            editItem.text.toString().trim(),
                            selectItem.selectedItem.toString().trim()
                        )
                    }

                    if(modelAdd.variete?.length?:0 > 0){
                        itemList?.forEach {
                            if (it.variete?.uppercase() == modelAdd.variete?.uppercase() && it.nombre == modelAdd.nombre) {
                                ToastUtils.showShort("Cet élément est déja ajouté")

                                return@setOnClickListener
                            }
                        }

                        itemList?.add(modelAdd)
                        itemAdapter?.notifyDataSetChanged()

                        selectItem.setSelection(0)
                        editItem.text?.clear()
                    }
                    //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

        }

        fun setSpinnerAndSpinnerRV(
            context: Activity,
            recyclerList: RecyclerView,
            addBtn: AppCompatButton,
            selectItem2: AppCompatSpinner,
            selectItem: AppCompatSpinner,
            libelle: String = "Libellé",
            valeur: String = "Valeur",
            libeleList:MutableList<String> = arrayListOf(), valueList:MutableList<String> = arrayListOf() ) {
            val itemList = mutableListOf<OmbrageVarieteModel>()
            var countN = 0
            libeleList.forEach {
                itemList.add(OmbrageVarieteModel(0, it, valueList.get(countN)))
                countN++
            }
            val itemAdapter = OmbrageAdapter(itemList, libelle, valeur)
            try {
                recyclerList.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerList.adapter = itemAdapter
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

            addBtn.setOnClickListener {
                try {
                    if (selectItem2.isSpinnerEmpty() || selectItem.isSpinnerEmpty()
                    ) {
                        Commons.showMessage("Renseignez des données sur l'autre insecte, svp !", context, callback = {})
                        return@setOnClickListener
                    }

                    val modelAdd = OmbrageVarieteModel(
                        0,
                        selectItem2.getSpinnerContent(),
                        selectItem.getSpinnerContent().trim()
                    )

                    if(modelAdd.variete?.length?:0 > 0){
                        itemList?.forEach {
                            if (it.variete?.uppercase() == modelAdd.variete?.uppercase() && it.nombre == modelAdd.nombre) {
                                ToastUtils.showShort("Cet élément est déja ajouté")

                                return@setOnClickListener
                            }
                        }

                        itemList?.add(modelAdd)
                        itemAdapter?.notifyDataSetChanged()

                        selectItem.setSelection(0)
                        selectItem2.setSelection(0)
                    }
                    //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

        }

        fun setFiveItremRV(
            context: Activity,
            recyclerList: RecyclerView,
            addBtn: AppCompatButton,
            selectNom: Spinner,
            selectContenant: Spinner,
            selectUnite: Spinner,
            editQte: AppCompatEditText,
            editFreq: AppCompatEditText,
            defaultItemSize: Int = 5,
            libeleList:MutableList<String> = arrayListOf(),
            valueList:MutableList<String> = arrayListOf() ) {
            val pesticideListSParcelle = mutableListOf<AdapterItemModel>()
            var countN = 0
//        libeleList.forEach {
//            pesticideListSParcelle.add(AdapterItemModel(0, it, valueList.get(countN)))
//            countN++
//        }
            var pesticideSParcelleAdapter: MultipleItemAdapter? = MultipleItemAdapter(
                pesticideListSParcelle
            )

            if(libeleList.size > 0){
                pesticideSParcelleAdapter = MultipleItemAdapter(pesticideListSParcelle, libeleList[0], libeleList[1], libeleList[2], libeleList[3], libeleList[4])
            }
            try {
                recyclerList.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerList.adapter = pesticideSParcelleAdapter
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

            addBtn.setOnClickListener {
                try {
                    if (selectNom.isSpinnerEmpty()
                        || selectContenant.isSpinnerEmpty()
                        || selectUnite.isSpinnerEmpty()
                        || editQte.text.toString().isNullOrBlank()
                        || editFreq.text.toString().isNullOrBlank()
                    ) {
                        Commons.showMessage("Renseignez des données, svp !", context, callback = {})
                        return@setOnClickListener
                    }

                    var pesticideParRav = AdapterItemModel(
                        0,
                        selectNom.getSpinnerContent(),
                        selectContenant.getSpinnerContent(),
                        selectUnite.getSpinnerContent(),
                        editQte.text.toString(),
                        editFreq.text.toString(),
                    )

                    if(defaultItemSize == 4){
                        pesticideParRav = AdapterItemModel(
                            0,
                            selectNom.getSpinnerContent(),
                            selectContenant.getSpinnerContent(),
                            editQte.text.toString(),
                            editFreq.text.toString(),
                            ""
                        )
                    }

                    if(defaultItemSize == 3){
                        pesticideParRav = AdapterItemModel(
                            0,
                            selectNom.getSpinnerContent(),
                            selectContenant.getSpinnerContent(),
                            editQte.text.toString(),
                            "",
                            "",
                        )
                    }

                    if(pesticideParRav.value?.length?:0 > 0){
                        pesticideListSParcelle?.forEach {
                            if (it.value?.uppercase() == pesticideParRav.value?.uppercase()) {
                                ToastUtils.showShort("Cet élément est déja ajouté")

                                return@setOnClickListener
                            }
                        }

                        pesticideListSParcelle?.add(pesticideParRav)
                        pesticideSParcelleAdapter?.notifyDataSetChanged()

                        selectNom.setSelection(0)
                        selectContenant.setSelection(0)
                        selectUnite.setSelection(0)
                        editQte.text?.clear()
                        editFreq.text?.clear()
                    }
                    //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

        }

        fun setSixItremRV(
            context: Activity,
            recyclerList: RecyclerView,
            addBtn: AppCompatButton,
            select: Spinner,
            select2: Spinner,
            select3: Spinner,
            edit: AppCompatEditText,
            edit2: AppCompatEditText,
            edit3: AppCompatEditText,
            defaultItemSize: Int = 5,
            libeleList:MutableList<String> = arrayListOf(),
            valueList:MutableList<String> = arrayListOf() ) {
            val pesticideListSParcelle = mutableListOf<AdapterItemModel>()
            var countN = 0

            var pesticideSParcelleAdapter: SixItemAdapter? = SixItemAdapter(
                pesticideListSParcelle
            )

            if(libeleList.size > 0){
                pesticideSParcelleAdapter = SixItemAdapter(pesticideListSParcelle,
                    libeleList[0], libeleList[1],
                    libeleList[2], libeleList[3],
                    libeleList[4], libeleList[5])
            }
            try {
                recyclerList.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerList.adapter = pesticideSParcelleAdapter
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

            addBtn.setOnClickListener {
                try {
                    if (select.isSpinnerEmpty()
                        || select2.isSpinnerEmpty()
                        || select3.isSpinnerEmpty()
                        || edit.text.toString().isNullOrBlank()
                        || edit2.text.toString().isNullOrBlank()
                        || edit3.text.toString().isNullOrBlank()
                    ) {
                        Commons.showMessage("Renseignez des données, svp !", context, callback = {})
                        return@setOnClickListener
                    }

                    var pesticideParRav = AdapterItemModel(
                        0,
                        select.getSpinnerContent(),
                        select2.getSpinnerContent(),
                        edit.text.toString(),
                        select3.getSpinnerContent(),
                        edit2.text.toString(),
                        edit3.text.toString(),
                    )

                    if(pesticideParRav.value?.length?:0 > 0){
                        pesticideListSParcelle?.forEach {
                            if (it.value?.uppercase() == pesticideParRav.value?.uppercase()) {
                                ToastUtils.showShort("Cet élément est déja ajouté")

                                return@setOnClickListener
                            }
                        }

                        pesticideListSParcelle?.add(pesticideParRav)
                        pesticideSParcelleAdapter?.notifyDataSetChanged()

                        select.setSelection(0)
                        select2.setSelection(0)
                        select3.setSelection(0)
                        edit.setSelection(0)
                        edit2.text?.clear()
                        edit3.text?.clear()
                    }
                    //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

        }

        fun setNineItremRV(
            context: Activity,
            recyclerList: RecyclerView,
            addBtn: AppCompatButton,
            select: Spinner?,
            select2: Spinner?,
            select3: Spinner?,
            select4: Spinner?,
            select5: Spinner?,
            edit: AppCompatEditText?,
            edit2: AppCompatEditText?,
            edit3: AppCompatEditText?,
            edit4: AppCompatEditText?,
            edit5: AppCompatEditText?,
            engageItem: Int = 1, //0 = same, 1 = 5 slect, 2 = 5 edit
            defaultItemSize: Int = 9,
            libeleList:MutableList<String> = arrayListOf(),
            valueList:MutableList<String> = arrayListOf() ) {
            val pesticideListSParcelle = mutableListOf<AdapterItemModel>()
            var countN = 0

            var pesticideSParcelleAdapter: NineItemAdapter? = NineItemAdapter(
                pesticideListSParcelle
            )

            if(libeleList.size > 0){
                pesticideSParcelleAdapter = NineItemAdapter(pesticideListSParcelle,
                    libeleList[0], libeleList[1],
                    libeleList[2], libeleList[3],
                    libeleList[4], libeleList[5],
                    libeleList[6], libeleList[7],
                    libeleList[8]
                )
            }
            try {
                recyclerList.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerList.adapter = pesticideSParcelleAdapter
            } catch (ex: Exception) {
                LogUtils.e(ex.message)
                FirebaseCrashlytics.getInstance().recordException(ex)
            }

            addBtn.setOnClickListener {
                try {
                    var pesticideParRav = AdapterItemModel(0)
                    if(engageItem == 1){
                        if (select?.isSpinnerEmpty() == true
                            || select2?.isSpinnerEmpty() == true
                            || select3?.isSpinnerEmpty() == true
                            || select4?.isSpinnerEmpty() == true
                            || select5?.isSpinnerEmpty() == true
                            || edit?.text.toString().isNullOrBlank()
                            || edit2?.text.toString().isNullOrBlank()
                            || edit3?.text.toString().isNullOrBlank()
                            || edit4?.text.toString().isNullOrBlank()
                        ) {
                            Commons.showMessage("Renseignez des données, svp !", context, callback = {})
                            return@setOnClickListener
                        }

                        pesticideParRav = AdapterItemModel(
                            0,
                            select?.getSpinnerContent(),
                            select2?.getSpinnerContent(),
                            edit?.text.toString(),
                            select3?.getSpinnerContent(),
                            edit2?.text.toString(),
                            select4?.getSpinnerContent(),
                            edit3?.text.toString(),
                            select5?.getSpinnerContent(),
                            edit4?.text.toString(),
                        )
                    }else if(engageItem == 2){
                        if (select?.isSpinnerEmpty() == true
                            || select2?.isSpinnerEmpty() == true
                            || select3?.isSpinnerEmpty() == true
                            || select4?.isSpinnerEmpty() == true
                            || edit?.text.toString().isNullOrBlank()
                            || edit2?.text.toString().isNullOrBlank()
                            || edit3?.text.toString().isNullOrBlank()
                            || edit4?.text.toString().isNullOrBlank()
                            || edit5?.text.toString().isNullOrBlank()
                        ) {
                            Commons.showMessage("Renseignez des données, svp !", context, callback = {})
                            return@setOnClickListener
                        }

                        pesticideParRav = AdapterItemModel(
                            0,
                            select?.getSpinnerContent(),
                            select2?.getSpinnerContent(),
                            edit?.text.toString(),
                            edit5?.text.toString(),
                            edit2?.text.toString(),
                            select3?.getSpinnerContent(),
                            edit3?.text.toString(),
                            select4?.getSpinnerContent(),
                            edit4?.text.toString(),
                        )
                    }

                    if(pesticideParRav.value?.length?:0 > 0){
                        pesticideListSParcelle?.forEach {
                            if (it.value?.uppercase() == pesticideParRav.value?.uppercase()) {
                                ToastUtils.showShort("Cet élément est déja ajouté")

                                return@setOnClickListener
                            }
                        }

                        pesticideListSParcelle?.add(pesticideParRav)
                        pesticideSParcelleAdapter?.notifyDataSetChanged()

                        if(engageItem == 1){
                            select?.setSelection(0)
                            select2?.setSelection(0)
                            select3?.setSelection(0)
                            select4?.setSelection(0)
                            select5?.setSelection(0)
                            edit?.setSelection(0)
                            edit2?.text?.clear()
                            edit3?.text?.clear()
                            edit4?.text?.clear()
                        }else if(engageItem == 2){
                            select?.setSelection(0)
                            select2?.setSelection(0)
                            select3?.setSelection(0)
                            select4?.setSelection(0)

                            edit?.setSelection(0)
                            edit2?.text?.clear()
                            edit3?.text?.clear()
                            edit4?.text?.clear()
                            edit5?.text?.clear()
                        }

                    }
                    //addVarieteArbre(varieteArbre, varieteArbrListSParcelle, varieteArbrSParcelleAdapter)
                } catch (ex: Exception) {
                    LogUtils.e(ex.message)
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }

        }

        fun setupItemMultiSelection(
            context: Activity,
            selectItemMulti: MultiSelectSpinner,
            title: String  = "Faites vos choix !",
            itemList: List<CommonData>,
            currentList : MutableList<String> = mutableListOf(),
            onValueChanged: ((MutableList<String>) -> Unit)) {

            var listSelectVarieteArbrePosList = mutableListOf<Int>()
            var listSelectVarieteArbreList = mutableListOf<String>()

            var indItem = 0
            (itemList)?.forEach {
                if(currentList.size > 0){ if(currentList.contains(it.nom)) listSelectVarieteArbrePosList.add(indItem) }
                indItem++
            }

            selectItemMulti.setTitle(title)
            selectItemMulti.setItems(itemList.map { it.nom })
            //multiSelectSpinner.hasNoneOption(true)
            selectItemMulti.setSelection(listSelectVarieteArbrePosList.toIntArray())
            selectItemMulti.setListener(object : MultiSelectSpinner.OnMultipleItemsSelectedListener {
                override fun selectedIndices(indices: MutableList<Int>?) {
                    listSelectVarieteArbrePosList.clear()
                    listSelectVarieteArbrePosList.addAll(indices?.toMutableList() ?: mutableListOf())
                }

                override fun selectedStrings(strings: MutableList<String>?) {
                    listSelectVarieteArbreList.clear()
                    listSelectVarieteArbreList.addAll(strings?.toMutableList() ?: arrayListOf())

                    onValueChanged(listSelectVarieteArbreList)
                }

            })
        }

        fun printModelValue(producteurMenage: Object, mapEntries: List<MapEntry>?) {

            LogUtils.json(producteurMenage)
            LogUtils.e("*********************************************************")
            LogUtils.json(ArrayList(mapEntries))

        }

        fun getFileExtension(filePath: String): String {
            val mimeTypeMap = MimeTypeMap.getSingleton()
            val extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
            return mimeTypeMap.getMimeTypeFromExtension(extension)?.split("/")?.lastOrNull() ?: ""
        }

        fun getFilePathFromUri(uri: Uri, context: Context): String? {
            val documentFile = DocumentFile.fromSingleUri(context, uri)
            return documentFile?.uri?.path
        }

        fun copyFile(sourceUri: Uri?, pPath: String?, context: Context) {
            try {
                // Open an input stream from the selected file URI
                val inputStream: InputStream? = context.getContentResolver().openInputStream(
                    sourceUri!!
                )

                // Create the destination file
                val destinationFile = File(pPath)

                // Open an output stream to the destination file
                val outputStream: OutputStream = FileOutputStream(destinationFile)

                // Copy the bytes from the input stream to the output stream
                val buffer = ByteArray(1024)
                var length: Int
                while ( inputStream?.read(buffer).also { length = it!! }!! > 0) {
                    outputStream.write(buffer, 0, length)
                }

                inputStream?.close()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
                LogUtils.e(e.message)
            }
        }

        fun getParcelleNotSyncLibel(parcelle: ParcelleModel): String? {

            return if(parcelle.codeParc.isNullOrEmpty() == false) parcelle.codeParc.toString() else "CODE EN COUR, N "+(parcelle.uid)

        }

        fun formatTitleOfNavView(title: String?): String? {
            return title?.let {
                var returner = it
                if(it.contains(" ")){
                    val velo = it.split(" ".toRegex(), it.lastIndexOf(" "))
                    if(velo.size > 1)
                        returner = velo[0].plus("\n"+velo[1])
                    else
                        returner = velo[0]
                }
                returner
            }
        }

        fun invertValue(s: String, s1: String, rolesCopy: MutableList<String>): MutableList<String> {
            val index1 = rolesCopy.indexOf(s)
            val index2 = rolesCopy.indexOf(s1)

            if (index1 != -1 && index2 != -1) {
                val newList = rolesCopy.toMutableList()
                newList[index1] = rolesCopy[index2]
                newList[index2] = rolesCopy[index1]
                return newList
            }
            return rolesCopy
        }

        fun adjustTextViewSizesInDialogExt(Context: Context, builder: AlertDialog.Builder, _title: String, Dimension: Float, isTitle: Boolean) {
            Commons.adjustTextViewSizesInDialog(Context, builder, _title, Dimension, isTitle)
        }

        fun showCircularIndicator(
            libelle: String = "TAUX DE REPONSE:",
            snackProgressBarManager: SnackProgressBarManager?,
            positionBario: Pair<Int, Int> = Pair(0, 1),
            displayId: Int = 2510,
            buttonLib: String = "",
            callback: (() -> Unit?)? = null,
        ) {

            var keyCount = positionBario.first
            var valCount = positionBario.second
            var positionBar = 0

            if(keyCount>=valCount && keyCount!=0){
                val divide = (valCount.toDouble()/keyCount.toDouble())
                var tauxFif = (divide.times(100))
                positionBar = tauxFif.roundToInt()
            }

            val circularTypeWithAction =
                SnackProgressBar(SnackProgressBar.TYPE_CIRCULAR, libelle)
                    .setIsIndeterminate(false)
                    .setProgressMax(100)
                    .setShowProgressPercentage(true)
                    .setSwipeToDismiss(true)
                    .setIconResource(R.mipmap.ic_launcher)
                    .setShowProgressPercentage(true)

            if(buttonLib.isNotEmpty()){
                circularTypeWithAction.setAction(buttonLib, object : SnackProgressBar.OnActionClickListener{
                    override fun onActionClick() {
                        callback?.invoke()
                    }

                })
            }


            snackProgressBarManager?.setActionTextColor(R.color.ccb_belge)
            snackProgressBarManager?.show(circularTypeWithAction, SnackProgressBarManager.LENGTH_LONG, displayId)
            snackProgressBarManager?.setProgress(progress = positionBar)
//            object : CountDownTimer((positionBar*15).toLong(), positionBar.toLong()) {
//                var i = 0
//
//                override fun onTick(millisUntilFinished: Long) {
//                    i++
//                    //snackProgressBarManager?.setProgress(i)
//                }
//
//                override fun onFinish() {
//                    snackProgressBarManager?.dismiss()
//                }
//            }.start()

        }

        fun defineSnackBarManager(snackProgressBarManager: SnackProgressBarManager, linearActionContainerInspection: View, ctx: Context): SnackProgressBarManager? {

            return snackProgressBarManager.setViewToMove(linearActionContainerInspection)
                // (Optional) Change progressBar color, default = R.color.colorAccent
                .setProgressBarColor(R.color.green_3)
                .setBackgroundColor(R.color.ccb_green)
                // (Optional) Change text size, default = 14sp
                .setTextSize(ctx.resources.getDimension(R.dimen._8ssp))
                // (Optional) Set max lines, default = 2
                .setProgressTextColor(R.color.white_color)
                .useRoundedCornerBackground(true)
                .setMessageMaxLines(2)
                // (Optional) Register onDisplayListener
                .setOnDisplayListener(object : SnackProgressBarManager.OnDisplayListener {
                    override fun onLayoutInflated(
                        snackProgressBarLayout: SnackProgressBarLayout,
                        overlayLayout: FrameLayout,
                        snackProgressBar: SnackProgressBar,
                        onDisplayId: Int
                    ) {

                    }

                    override fun onShown(snackProgressBar: SnackProgressBar, onDisplayId: Int) {
                        if (onDisplayId == 5000) {
                        }
                    }

                    override fun onDismissed(snackProgressBar: SnackProgressBar, onDisplayId: Int) {
                    }
                })

        }

        fun applyChartSetting(context: Context, chart: BarChart?, data: MutableList<Data>) {

            val onValueSelectedRectF = RectF()

            chart?.setOnChartValueSelectedListener(object: OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e == null) return

                    val bounds: RectF = onValueSelectedRectF
                    chart!!.getBarBounds(e as BarEntry?, bounds)
                    val position = chart!!.getPosition(e, YAxis.AxisDependency.LEFT)

                    Log.i("bounds", bounds.toString())
                    Log.i("position", position.toString())

                    Log.i(
                        "x-index",
                        "low: " + chart!!.lowestVisibleX + ", high: "
                                + chart!!.highestVisibleX
                    )

                    MPPointF.recycleInstance(position)
                }

                override fun onNothingSelected() {

                }

            })

            chart?.setDrawBarShadow(false)
            chart?.setDrawValueAboveBar(true)
            chart?.getDescription()?.setEnabled(false)
            chart?.setMaxVisibleValueCount(60)
            chart?.setPinchZoom(false)
            chart?.setDrawGridBackground(false)
            chart?.isDoubleTapToZoomEnabled = false
            chart?.setScaleEnabled(false)

            val xAxisFormatter: ValueFormatter = object: ValueFormatter() {
                override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                    val vallo = data[Math.min(Math.max(value.toInt(), 0), data.size - 1)].xAxisValue
                    LogUtils.d(vallo)
                    return vallo
                }

            }

            val xAxis: XAxis = chart!!.getXAxis()
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.typeface = Typeface.DEFAULT
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f // only intervals of 1 day
            xAxis.labelCount = 7
            xAxis.isEnabled = false
            xAxis.setValueFormatter(xAxisFormatter)

//        xAxis.axisMaximum = dataC.xMax + 0.25f

            val custom: ValueFormatter = MyAxisValueFormatter()

            val leftAxis: YAxis = chart!!.getAxisLeft()
            leftAxis.typeface = Typeface.DEFAULT
            leftAxis.setLabelCount(8, false)
            leftAxis.setValueFormatter(custom)
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            leftAxis.spaceTop = 15f
            leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


            chart?.axisRight?.isEnabled = false
            /*val rightAxis = barChart.axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.setLabelCount(8, false)
            rightAxis.valueFormatter = custom
            rightAxis.spaceTop = 15f
            rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)*/


            val l = chart?.legend
            l!!.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            l.orientation = Legend.LegendOrientation.HORIZONTAL
            l.setDrawInside(false)
            l.form = Legend.LegendForm.NONE
            l.formSize = 9f
            l.textSize = 11f
            l.xEntrySpace = 1f

            val mv = XYMarkerView(context, xAxisFormatter)
            mv.chartView = chart // For bounds control
            chart!!.marker = mv

        }

        fun setData(chartData: BarChart?, dataList: List<Data>) {
            val entries1 = ArrayList<BarEntry>()
            val entries2 = ArrayList<BarEntry>()
            val listBar = ArrayList<String>()
            val listBar2 = ArrayList<String>()
            val listBarColor = ArrayList<Int>()
            val listBarColor2 = ArrayList<Int>()

            dataList.forEach {
                entries1.add(BarEntry(it.xValue, it.yValue))
                listBar.add(it.xAxisValue)
                listBarColor.add(it.xAxisColor)
            }

            val set: BarDataSet
            if (chartData!!.data != null &&
                chartData!!.data.dataSetCount > 0
            ) {
                set = chartData!!.data.getDataSetByIndex(0) as BarDataSet
                set.values = entries1
                chartData!!.data.notifyDataChanged()
                chartData!!.notifyDataSetChanged()
            } else {
                set = BarDataSet(entries1, "${listBar.toModifString(true, " - ")}")
                set.colors = listBarColor
                set.setValueTextColors(listBarColor)
                val data = BarData(set)
                data.setValueTextSize(11f)
                data.setValueTypeface(Typeface.DEFAULT)
                data.setValueFormatter(ValueFormatter22())
                data.barWidth = 0.8f
                chartData!!.setData(data)
                chartData!!.invalidate()
            }
        }

        fun logErrorToFile(producteur: Any) {
            LogUtils.file(GsonUtils.toJson(producteur))
//
            LogUtils.d(LogUtils.getCurrentLogFilePath())
        }

    }


}

data class MapEntry(val key: String, val value: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(key)
        parcel.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MapEntry> {
        override fun createFromParcel(parcel: Parcel): MapEntry {
            return MapEntry(parcel)
        }

        override fun newArray(size: Int): Array<MapEntry?> {
            return arrayOfNulls(size)
        }
    }
}
