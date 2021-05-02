import javafx.geometry.Bounds
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import kotlin.random.Random


typealias Stencil = (GraphicsContext.() -> Unit) -> Unit


fun randomLocationWithin(bounds: Bounds) = Vector(
    Random.nextDouble() * bounds.width + bounds.minX,
    Random.nextDouble() * bounds.height + bounds.minY
)

val Bounds.center get() = Vector(centerX, centerY)

val Bounds.max get() = v(maxX, maxY)

val Canvas.stencil: Stencil
    get() = { composition ->
        graphicsContext2D.save()
        composition(graphicsContext2D)
        graphicsContext2D.restore()
    }

