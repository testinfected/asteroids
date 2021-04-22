import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


class Ship(var pos: Point2D, var angle: Double = 0.0)

class Asteroid(
    var pos: Point2D,
    var angle: Double,
    var scale: Double,
    val shape: Array<Point2D>
)

class Splat(
    val pos: Point2D,
    val born: Long,
    val shape: Array<Point2D>,
)

val splat = arrayOf(
    Point2D(-2.0, 0.0),
    Point2D(-2.0, -2.0),
    Point2D(2.0, -2.0),
    Point2D(3.0, 1.0),
    Point2D(2.0, -1.0),
    Point2D(0.0, 2.0),
    Point2D(1.0, 3.0),
    Point2D(-1.0, 3.0),
    Point2D(-4.0, -1.0),
    Point2D(-3.0, .0),
)

val rocks =
    arrayOf(
        arrayOf(
            Point2D(0.0, -2.0),
            Point2D(2.0, -4.0),
            Point2D(4.0, -2.0),
            Point2D(3.0, 0.0),
            Point2D(4.0, 2.0),
            Point2D(1.0, 4.0),
            Point2D(-2.0, 4.0),
            Point2D(-4.0, 2.0),
            Point2D(-4.0, -2.0),
            Point2D(-2.0, -4.0)
        ),
        arrayOf(
            Point2D(2.0, -1.0),
            Point2D(4.0, -2.0),
            Point2D(2.0, -4.0),
            Point2D(0.0, -3.0),
            Point2D(-2.0, -4.0),
            Point2D(-4.0, -2.0),
            Point2D(-3.0, 0.0),
            Point2D(-4.0, 2.0),
            Point2D(-2.0, 4.0),
            Point2D(-1.0, 3.0),
            Point2D(2.0, 4.0),
            Point2D(4.0, 1.0)
        ),
        arrayOf(
            Point2D(-2.0, 0.0),
            Point2D(-4.0, 1.0),
            Point2D(-2.0, 4.0),
            Point2D(0.0, 1.0),
            Point2D(0.0, 4.0),
            Point2D(2.0, 4.0),
            Point2D(4.0, 1.0),
            Point2D(4.0, -1.0),
            Point2D(2.0, -4.0),
            Point2D(-1.0, -4.0),
            Point2D(-4.0, -1.0),
            Point2D(-2.0, 0.0),
        ),
        arrayOf(
            Point2D(1.0, 0.0),
            Point2D(4.0, -1.0),
            Point2D(4.0, -2.0),
            Point2D(1.0, -4.0),
            Point2D(-2.0, -4.0),
            Point2D(-1.0, -2.0),
            Point2D(-4.0, -2.0),
            Point2D(-4.0, 1.0),
            Point2D(-2.0, 4.0),
            Point2D(1.0, 3.0),
            Point2D(2.0, 4.0),
            Point2D(4.0, 2.0),
            Point2D(1.0, 0.0)
        )
    )

@ExperimentalTime
class Asteroids : Application() {

    private val grey: Paint = Color.rgb(40, 40, 50)
    private val velocity = Point2D(1.5, 0.0)

    private val inputs = arrayListOf<KeyCode>()

    val asteroids = mutableListOf<Asteroid>()
    val splats = mutableListOf<Splat>()

    override fun start(stage: Stage) {
        stage.title = "Asteroids"

        val canvas = set(stage)
        val ship = makeShip(canvas.layoutBounds)
        asteroids += (1..4).map { makeAsteroid(randomLocationOn(canvas)) }

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                save(canvas)
                clear(canvas)
                draw(ship, canvas)
                move(ship)
                drawAsteroids(canvas)
                moveAsteroids(canvas)
                splitAsteroids(now)
                drawSplats(now, canvas)
                restore(canvas)
            }
        }

        timer.start()
        stage.show()
    }

    private fun set(stage: Stage): Canvas {
        val canvas = Canvas(1344.0, 840.0)
        val scene = Scene(Group(canvas))
        stage.scene = scene
        scene.onKeyPressed = EventHandler {
            if (it.code !in inputs) inputs.add(it.code)
        }

        scene.onKeyReleased = EventHandler {
            inputs.remove(it.code)
        }

        return canvas
    }

    fun clear(canvas: Canvas) {
        val graphics = canvas.graphicsContext2D.apply {
            fill = grey
        }

        graphics.fillRect(0.0, 0.0, canvas.width, canvas.height)
    }

    private fun restore(canvas: Canvas) {
        canvas.graphicsContext2D.restore()
    }

    private fun save(canvas: Canvas) {
        canvas.graphicsContext2D.save()
    }

    private fun move(ship: Ship) {
        val delta = when {
            KeyCode.RIGHT in inputs -> 1
            KeyCode.LEFT in inputs -> -1
            else -> 0
        }
        ship.angle += delta
    }

    private fun makeShip(bounds: Bounds) = Ship(Point2D(bounds.centerX, bounds.centerY))

    private fun draw(ship: Ship, canvas: Canvas) {
        val graphics = canvas.graphicsContext2D
        graphics.save()

        graphics.apply {
            fill = Color.TRANSPARENT
            stroke = Color.WHITE
            lineWidth = 2.0
        }

        val dx = 10.0
        val dy = 6.0

        graphics.translate(ship.pos.x, ship.pos.y)
        graphics.rotate(ship.angle)

        graphics.beginPath()
        graphics.moveTo(dx, 0.0)
        graphics.lineTo(-dx, dy)
        graphics.lineTo(-dx, -dy)
        graphics.closePath()
        graphics.stroke()

        graphics.restore()
    }

    private fun makeAsteroid(pos: Point2D, scale: Double = 16.0) = Asteroid(
        pos = pos,
        angle = Random.nextDouble().times(360),
        scale,
        shape = rocks[Random.nextInt(4)]
    )

    private fun randomLocationOn(canvas: Canvas) = randomLocationWithin(canvas.layoutBounds)

    private fun randomLocationWithin(bounds: Bounds) =
        Point2D(Random.nextDouble() * bounds.width + bounds.minX, Random.nextDouble() * bounds.height + bounds.minY)

    private fun drawAsteroids(canvas: Canvas) {
        for (asteroid in asteroids) draw(asteroid, canvas)
    }

    private fun moveAsteroids(canvas: Canvas) {
        for (asteroid in asteroids) move(asteroid, canvas)
    }

    private fun splitAsteroids(now: Long) {
        for (asteroid in asteroids.toTypedArray()) {
            split(asteroid, now)
        }
    }

    private fun draw(asteroid: Asteroid, canvas: Canvas): Unit = draw(asteroid, canvas.graphicsContext2D)

    private fun draw(asteroid: Asteroid, graphics: GraphicsContext) {
        graphics.save()

        graphics.apply {
            fill = Color.TRANSPARENT
            stroke = Color.WHITE
            lineWidth = 1.0 / asteroid.scale
        }

        graphics.translate(asteroid.pos.x, asteroid.pos.y)
        graphics.scale(asteroid.scale, asteroid.scale)

        graphics.beginPath()
        asteroid.shape.forEachIndexed { index, vertex ->
            when (index) {
                0 -> graphics.moveTo(vertex.x, vertex.y)
                else -> graphics.lineTo(vertex.x, vertex.y)
            }
        }
        graphics.closePath()
        graphics.stroke()

        graphics.restore()
    }

    private fun move(asteroid: Asteroid, canvas: Canvas): Unit = move(asteroid, canvas.layoutBounds)

    private fun move(asteroid: Asteroid, bounds: Bounds) {
        asteroid.pos = warp(step(asteroid), Point2D(bounds.maxX, bounds.maxY))
    }

    private fun split(asteroid: Asteroid, now: Long) {
        if (Random.nextInt(until = 960) != 1) return
        if (asteroid.scale == 4.0) return

        val parts = (1..2).map {
            makeAsteroid(asteroid.pos, scale = asteroid.scale / 2)
        }

        asteroids -= asteroid
        asteroids += parts
        splats += Splat(asteroid.pos, born = now, shape = splat)
    }

    private fun drawSplats(now: Long, canvas: Canvas) {
        for (splat in splats.toTypedArray()) draw(splat, now, canvas)
    }

    private fun draw(splat: Splat, now: Long, canvas: Canvas): Unit = draw(splat, now, canvas.graphicsContext2D)

    private fun draw(splat: Splat, now: Long, graphics: GraphicsContext) {
        graphics.save()

        graphics.apply {
            stroke = Color.WHITE
            fill = Color.WHITE
            lineWidth = 0.5
        }

        graphics.translate(splat.pos.x, splat.pos.y)
        graphics.scale(2.0, 2.0)

        val size = 1 + (now - splat.born).toDuration(DurationUnit.NANOSECONDS).inSeconds

        splat.shape.forEach { point ->
            graphics.strokeOval(point.x * size, point.y * size, 1.0, 1.0)
        }

        if (size > 5) splats -= splat

        graphics.restore()
    }

    private fun step(asteroid: Asteroid): Point2D {
        val step = Rotate(asteroid.angle).transform(velocity)
        return asteroid.pos.add(step)
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(Asteroids::class.java, *args)
        }
    }
}


fun warp(pos: Point2D, bounds: Point2D): Point2D {
    return Point2D(warp(pos.x, bounds.x), warp(pos.y, bounds.y))
}

fun warp(value: Double, bound: Double): Double {
    return (value + bound) % bound
}
