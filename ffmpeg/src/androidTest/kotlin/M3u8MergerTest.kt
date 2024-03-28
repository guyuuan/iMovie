import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.chitanda.lib.ffmpeg.knative.M3u8Merger
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author: Chen
 * @createTime: 3/22/24 18:18
 * @description:
 **/
@RunWith(AndroidJUnit4::class)
class M3u8MergerTest {
    private lateinit var merger: M3u8Merger
    @Before
    fun init(){
        merger=M3u8Merger()
    }
    @Test
    fun me3u8MergerInit()= runTest {
        val merger = M3u8Merger()
        merger.init()
    }
}