import javafx.geometry.Bounds
import kotlin.random.Random


fun randomLocationWithin(bounds: Bounds) = Vector(
    Random.nextDouble() * bounds.width + bounds.minX,
    Random.nextDouble() * bounds.height + bounds.minY
)

val Bounds.center get() = Vector(centerX, centerY)

val Bounds.max get() = v(maxX, maxY)

