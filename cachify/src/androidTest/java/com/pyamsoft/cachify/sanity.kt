import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@SmallTest
public class AndroidSanity {

    @Test
    public fun sanity(): Unit = runTest { assertEquals(3 + 3, 6) }
}
