package ci.progbandama.mobile.tests

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ci.progbandama.mobile.R
import ci.progbandama.mobile.tools.Commons
import com.blankj.utilcode.util.ToastUtils

class TestLabActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_lab)


        Commons.showMessage(
            "Clic sur mon",
            this,
            finished = false,
            callback = { ToastUtils.showShort("merci") },
            deconnec = false
        )
    }
}
