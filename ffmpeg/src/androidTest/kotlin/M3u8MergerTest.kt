import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.chitanda.lib.ffmpeg.knative.M3u8Merger
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author: Chen
 * @createTime: 3/22/24 18:18
 * @description:
 **/
@RunWith(AndroidJUnit4::class)
class M3u8MergerTest {
    @Test
    fun me3u8MergerInit() {
        val merger = M3u8Merger()
        merger.init()
    }
}