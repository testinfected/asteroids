import javafx.geometry.Point2D
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AsteroidsTest {

    @Test
    fun `keeping within to bounds`() {
        assertEquals(100.0, warp(100.0, 1000.0))
        assertEquals(0.0, warp(1000.0, 1000.0))
        assertEquals(1.0, warp(1001.0, 1000.0))
        assertEquals(0.0, warp(0.00, 1000.0))
        assertEquals(999.0, warp(-1.00, 1000.0))
    }

    @Test
    fun `warping through scene`() {
        assertEquals(
            Point2D(500.0, 500.0), warp(Point2D(500.0, 500.0), Point2D(1000.0, 1000.0)), "center")
        assertEquals(
            Point2D(1.0, 999.0), warp(Point2D(1001.0, -1.0), Point2D(1000.0, 1000.0)), "bottom left")
        assertEquals(
            Point2D(1.0, 1.0), warp(Point2D(1001.0, 1001.0), Point2D(1000.0, 1000.0)), "top left")
        assertEquals(
            Point2D(999.0, 1.0), warp(Point2D(-1.0, 1.0), Point2D(1000.0, 1000.0)), "top right")
        assertEquals(
            Point2D(999.0, 999.0), warp(Point2D(-1.0, -1.0), Point2D(1000.0, 1000.0)), "bottom right")
    }
}