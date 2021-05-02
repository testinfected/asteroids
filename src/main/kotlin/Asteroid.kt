import javafx.scene.transform.Rotate
import kotlin.random.Random

class Asteroid(
    var pos: Vector,
    var scale: Double,
    val velocity: Vector,
    val shape: Array<Vector>
) {

    fun update(space: Space) {
        pos = space.warp(pos.add(velocity))
    }

    companion object {
        fun spawnAt(pos: Vector, scale: Double) = Asteroid(
            pos,
            scale,
            velocity = Rotate(Random.nextDouble(360.0)).transform(Vector(1.5, 0.0)),
            shape = rocks[Random.nextInt(4)]
        )
    }
}

class Splat(
    val pos: Vector,
    val born: Long,
    val angle: Double,
    val shape: Array<Vector>,
)

val splat = arrayOf(
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
