import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class Ship(var pos: Vector, var angle: Double = 0.0) {
    var firing = false

    fun render(graphics: GraphicsContext) {
        graphics.apply {
            fill = Color.TRANSPARENT
            stroke = Color.WHITE
            lineWidth = 1.0
        }

        val dx = 10.0
        val dy = 6.0

        graphics.translate(pos.x, pos.y)
        graphics.rotate(angle)

        graphics.beginPath()
        graphics.moveTo(dx, 0.0)
        graphics.lineTo(-dx, dy)
        graphics.lineTo(-dx, -dy)
        graphics.closePath()
        graphics.stroke()
    }

    companion object {
        fun spawnAt(pos: Vector) = Ship(pos)
    }
}

class Missile(var pos: Vector, val velocity: Vector, val born: Long)