import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PhysicsTest {

    @Test
    fun `keeps coordinates within bounds`() {
        assertEquals(100.0, warp(100.0, 1000.0))
        assertEquals(0.0, warp(1000.0, 1000.0))
        assertEquals(1.0, warp(1001.0, 1000.0))
        assertEquals(0.0, warp(0.00, 1000.0))
        assertEquals(999.0, warp(-1.00, 1000.0))
    }

    @Test
    fun `warps objects through bounds`() {
        assertEquals(v(500, 500), v(500, 500).warp(v(1000, 1000)), "center")
        assertEquals(v(1, 999), v(1001, -1).warp(v(1000, 1000)), "bottom left")
        assertEquals(v(1, 1), v(1001, 1001).warp(v(1000, 1000)), "top left")
        assertEquals(v(999, 1), v(-1, 1).warp(v(1000, 1000)), "top right")
        assertEquals(v(999, 999), v(-1, -1).warp(v(1000, 1000)), "bottom right")
    }
}