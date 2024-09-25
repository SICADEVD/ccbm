package ci.progbandama.mobile

import android.util.Base64
import ci.progbandama.mobile.tools.Constants
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun makeTextToBase64() {
//        println(String(java.util.Base64.getEncoder().encode(Constants.GIT_APP_UPURL.toByteArray() )))
    }

    @Test
    fun makeBase64ToText() {
//        println(decodeUpdate)
    }
}