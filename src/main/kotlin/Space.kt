import javafx.geometry.Bounds
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

class Space(private val bounds: Bounds) {
    val ship: Ship = Ship.spawnAt(pos = center)

    private val center get() = bounds.center

    fun randomLocation() = randomLocationWithin(bounds)

    fun warp(pos: Vector) = pos.warp(bounds.max)

    fun clear(stencil: Stencil) = stencil {
        fill = grey
        fillRect(0.0, 0.0, bounds.width, bounds.height)
    }

    fun renderShip(stencil: Stencil) = ship.render(stencil)

    companion object {
        val grey: Paint = Color.rgb(40, 40, 50)
    }
}


