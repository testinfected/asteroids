import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.transform.Rotate
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


interface ShipEventListener {
    fun missileFired(missile: Missile)
}


class Ship(
    private var pos: Vector,
    private var angle: Double = 0.0,
    private val inputs: Inputs,
) {
    val listeners = mutableListOf<ShipEventListener>()

    private var isFiring = false

    fun update(now: Long) {
        if (KeyCode.RIGHT in inputs) rotateRight()
        if (KeyCode.LEFT in inputs) rotateLeft()
        if (KeyCode.SPACE in inputs) fireMissile(now)
        if (KeyCode.SPACE !in inputs) holdFire()
    }

    fun draw(stencil: Stencil) = stencil {
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

    private fun rotateRight() {
        angle += 1
    }

    private fun rotateLeft() {
        angle -= 1
    }

    private fun fireMissile(now: Long) {
        if (isFiring) return

        isFiring = true
        notifyFired(Missile(pos, Rotate(angle).transform(v(2, 0)), born = now))
    }

    private fun holdFire() {
        isFiring = false
    }

    private fun notifyFired(missile: Missile) {
        listeners.forEach { it.missileFired(missile) }
    }

    companion object {
        private const val dx = 10.0
        private const val dy = 6.0

        fun spawnAt(pos: Vector, inputs: Inputs) = Ship(pos, inputs = inputs)
    }
}

class Missile(private var pos: Vector, private val velocity: Vector, val born: Long) {
    private var age: Duration = Duration.ZERO

    fun update(now: Long) {
        pos = pos.add(velocity)
        age = (now - born).toDuration(DurationUnit.NANOSECONDS)
    }

    fun draw(stencil: Stencil) = stencil {
        fill = Color.WHITE
        translate(pos.x, pos.y)
        fillOval(-1.0, -1.0, 2.0, 2.0)
    }

    fun hits(asteroid: Asteroid): Boolean {
        return asteroid.distanceTo(pos) < killDistance
    }

    fun olderThan(duration: Duration): Boolean {
        return age > duration
    }

    companion object {
        const val killDistance = 50.0
    }
}