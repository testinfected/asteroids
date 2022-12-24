import javafx.geometry.Point2D

typealias Vector = Point2D

fun v(x: Int, y:Int) = v(x.toDouble(), y.toDouble())

fun v(x: Double, y:Double) = Vector(x, y)


fun Vector.warp(bounds: Vector): Vector {
    return v(warp(x, bounds.x), warp(y, bounds.y))
}

fun warp(value: Double, bound: Double): Double {
    return (value + bound) % bound
}
