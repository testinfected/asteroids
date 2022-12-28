import javafx.geometry.Point2D
import javafx.scene.transform.Rotate
import kotlin.random.Random

typealias Vector = Point2D

fun v(x: Int, y:Int) = v(x.toDouble(), y.toDouble())

fun v(x: Double, y:Double) = Vector(x, y)

fun velocity(speed: Double) = Vector(speed, 0.0)

operator fun Vector.plus(other: Vector): Vector = add(other)

fun Vector.rotate(degrees: Double) = Rotate(degrees).transform(this)

fun Vector.warp(bounds: Vector): Vector {
    return v(warp(x, bounds.x), warp(y, bounds.y))
}

fun Vector.cappedTo(max: Double): Vector =
    if (magnitude() <= max) this else normalize().multiply(max)


fun warp(value: Double, bound: Double): Double {
    return (value + bound) % bound
}

fun randomAngle() = Random.nextDouble(360.0)