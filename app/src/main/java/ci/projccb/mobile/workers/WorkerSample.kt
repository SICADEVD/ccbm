package ci.projccb.mobile.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 11/06/2022.
 **/

class WorkerSample(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {


    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.
        /// uploadImages()
        // for (i in 1..1_000) LogUtils.e("Loop $i")
        // Indicate whether the work finished successfully with the Result
        //CommonUtils.isRooted()
        return Result.success()
    }

}
