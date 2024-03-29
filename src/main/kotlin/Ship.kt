import javafx.geometry.Bounds
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class Ship(
    private var pos: Vector,
    private var angle: Double = 0.0,
    private val inputs: Inputs,
) {
    val signals = Signaler()

    private var velocity = velocity(0.0)
    private var isFiring = false

    private val nosePosition: Vector
        get() = pos + v(dx, 0.0).rotate(angle)

    fun update(now: Long) {
        if (KeyCode.RIGHT in inputs) rotateRight()
        if (KeyCode.LEFT in inputs) rotateLeft()
        if (KeyCode.SPACE in inputs) fireMissile(now)
        if (KeyCode.SPACE !in inputs) holdFire()
        if (KeyCode.UP in inputs) accelerate()

        moveShip()
    }

    private fun accelerate() {
        velocity = (velocity + velocity(acceleration).rotate(angle)).cappedTo(maxVelocity)
    }

    private fun moveShip() {
        pos += velocity
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
        angle += rotationSpeed
    }

    private fun rotateLeft() {
        angle -= rotationSpeed
    }

    private fun fireMissile(now: Long) {
        if (isFiring) return

        isFiring = true
        val missile = Missile(nosePosition, velocity(missileSpeed).rotate(angle) + velocity, born = now)
        signals.announce(MissileFired(missile))
    }

    private fun holdFire() {
        isFiring = false
    }

    fun hits(asteroid: Asteroid): Boolean {
        return asteroid.collidesWith(pos)
    }

    fun keepInBounds(bounds: Bounds) {
        pos = pos.warp(bounds.max)
    }

    companion object {
        private const val dx = 10.0
        private const val dy = 6.0
        private const val maxVelocity = 3.0
        private const val acceleration = 0.015
        private const val rotationSpeed = 1.0
        private const val missileSpeed = 2.0

        fun spawnAt(pos: Vector, inputs: Inputs) = Ship(pos, inputs = inputs)
    }
}

class Missile(
    private var pos: Vector,
    private val velocity: Vector,
    val born: Long,
) {
    fun update(now: Long) {
        pos += velocity
    }

    fun shouldDie(now: Long): Boolean {
        return age(now) > maxLifeTime
    }

    private fun age(now: Long): Duration {
        return (now - born).toDuration(DurationUnit.NANOSECONDS)
    }

    fun draw(stencil: Stencil) = stencil {
        fill = Color.WHITE
        translate(pos.x, pos.y)
        fillOval(-1.0, -1.0, 2.0, 2.0)
    }

    fun hits(asteroid: Asteroid): Boolean {
        return asteroid.collidesWith(pos)
    }

    companion object {
        private val maxLifeTime = 3.seconds
    }
}