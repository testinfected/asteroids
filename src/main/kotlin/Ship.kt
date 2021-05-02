import javafx.scene.paint.Color

class Ship(var pos: Vector, var angle: Double = 0.0) {
    var firing = false

    fun render(stencil: Stencil) = stencil {
        fill = Color.TRANSPARENT
        stroke = Color.WHITE
        lineWidth = 1.0

        translate(pos.x, pos.y)
        rotate(angle)

        beginPath()
        moveTo(dx, 0.0)
        lineTo(-dx, dy)
        lineTo(-dx, -dy)
        closePath()
        stroke()
    }

    companion object {
        private const val dx = 10.0
        private const val dy = 6.0

        fun spawnAt(pos: Vector) = Ship(pos)
    }
}

class Missile(var pos: Vector, val velocity: Vector, val born: Long)