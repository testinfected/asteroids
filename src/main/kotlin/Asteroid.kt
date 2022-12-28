import javafx.geometry.Bounds
import javafx.scene.paint.Color
import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Asteroid(
    private var pos: Vector,
    private var scale: Double,
    private val velocity: Vector,
    private val shape: Array<Vector>
) {
    fun update(now: Long) {
        pos += velocity
    }

    fun split(now: Long): Pair<Iterable<Asteroid>, Splat> {
        val splat = Splat(pos, born = now, shape = splatShape, angle = randomAngle())

        if (scale == maxSize) {
            return emptyList<Asteroid>() to splat
        }

        val parts = (1..2).map {
            spawnAt(pos, scale = scale / 2)
        }

        return parts to splat
    }

    fun keepInBounds(bounds: Bounds) {
        pos = pos.warp(bounds.max)
    }

    fun draw(stencil: Stencil) = stencil {
        fill = Color.TRANSPARENT
        stroke = Color.WHITE
        lineWidth = 1.0 / scale

        translate(pos.x, pos.y)
        scale(scale, scale)

        beginPath()
        shape.forEachIndexed { index, vertex ->
            when (index) {
                0 -> moveTo(vertex.x, vertex.y)
                else -> lineTo(vertex.x, vertex.y)
            }
        }
        closePath()
        stroke()
    }

    // we could pass in a scoring function size -> Score or use a scoring table
    fun score(): Score {
        return when(scale) {
            16.0 -> Score(20)
            8.0 -> Score(50)
            else -> Score(100)
        }
    }

    fun collidesWith(other: Vector): Boolean {
        val distanceToCenter = other.distance(pos)

        return distanceToCenter <= when(scale) {
            16.0 -> 64
            8.0 -> 32
            else -> 16
        }
    }

    companion object {
        private const val maxSize = 4.0
        private const val expansionSpeed = 1.5

        fun spawnAt(pos: Vector, scale: Double) = Asteroid(
            pos,
            scale,
            velocity = velocity(expansionSpeed).rotate(randomAngle()),
            shape = rocks[Random.nextInt(rocks.size)]
        )
    }
}

class Splat(
    private val pos: Vector,
    private val born: Long,
    private val angle: Double,
    private val shape: Array<Vector>,
) {
    private var size: Double = 1.0

    fun update(now: Long) {
        size = 1 + lifeTime(now)
    }

    private fun lifeTime(now: Long) =
        (now - born).toDuration(DurationUnit.NANOSECONDS).toDouble(DurationUnit.SECONDS)

    fun shouldDie(now: Long): Boolean {
        return lifeTime(now) > maxLifeTime
    }

    fun draw(stencil: Stencil) = stencil {
        fill = Color.WHITE

        translate(pos.x, pos.y)
        scale(2.0, 2.0)
        rotate(angle)

        shape.forEach { point ->
            fillOval(point.x * size, point.y * size, 2 / size, 2 / size)
        }
    }

    companion object {
        private const val maxLifeTime = 4.0
    }
}

val splatShape = arrayOf(
    v(-2, 0),
    v(-2, -2),
    v(2, -2),
    v(3, 1),
    v(2, -1),
    v(0, 2),
    v(1, 3),
    v(-1, 3),
    v(-4, -1),
    v(-3, 0),
)

val rocks =
    arrayOf(
        arrayOf(
            v(0, -2),
            v(2, -4),
            v(4, -2),
            v(3, 0),
            v(4, 2),
            v(1, 4),
            v(-2, 4),
            v(-4, 2),
            v(-4, -2),
            v(-2, -4)
        ),
        arrayOf(
            v(2, -1),
            v(4, -2),
            v(2, -4),
            v(0, -3),
            v(-2, -4),
            v(-4, -2),
            v(-3, 0),
            v(-4, 2),
            v(-2, 4),
            v(-1, 3),
            v(2, 4),
            v(4, 1)
        ),
        arrayOf(
            v(-2, 0),
            v(-4, 1),
            v(-2, 4),
            v(0, 1),
            v(0, 4),
            v(2, 4),
            v(4, 1),
            v(4, -1),
            v(2, -4),
            v(-1, -4),
            v(-4, -1),
            v(-2, 0),
        ),
        arrayOf(
            v(1, 0),
            v(4, -1),
            v(4, -2),
            v(1, -4),
            v(-2, -4),
            v(-1, -2),
            v(-4, -2),
            v(-4, 1),
            v(-2, 4),
            v(1, 3),
            v(2, 4),
            v(4, 2),
            v(1, 0)
        )
    )
