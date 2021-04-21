import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import kotlin.random.Random


class Asteroid(var pos: Point2D, var angle: Double = 0.0)

class Ship(var pos: Point2D, var angle: Double = 0.0)

val rock = listOf(
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
)

class Asteroids : Application() {

    private val grey: Paint = Color.rgb(40, 40, 50)
    private val velocity = Point2D(1.5, 0.0)

    private val inputs = arrayListOf<KeyCode>()

    override fun start(stage: Stage) {
        stage.title = "Asteroids"

        val canvas = set(stage)
        val ship = makeShip(canvas)
        val asteroids = (1..10).map { makeAsteroid(canvas) }.toList()


        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                save(canvas)
                clear(canvas)
                draw(ship, canvas)
                move(ship)
                draw(asteroids, canvas)
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

    private fun makeShip(canvas: Canvas) = Ship(Point2D.ZERO.midpoint(Point2D(canvas.width, canvas.height)))

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

    private fun makeAsteroid(canvas: Canvas) = Asteroid(
        Point2D(Random.nextDouble() * canvas.width, Random.nextDouble() * canvas.height),
        Random.nextDouble().times(360)
    )

    private fun draw(asteroids: List<Asteroid>, canvas: Canvas) {
        for (asteroid in asteroids) {
            draw(asteroid, canvas)
            move(asteroid, canvas)
        }
    }

    private fun draw(asteroid: Asteroid, canvas: Canvas) {
        val graphics = canvas.graphicsContext2D
        graphics.save()

        val scale = 10.0
        graphics.apply {
            fill = Color.TRANSPARENT
            stroke = Color.WHITE
            lineWidth = 1.0 / scale
        }

        graphics.translate(asteroid.pos.x, asteroid.pos.y)
        graphics.scale(scale, scale)

        graphics.beginPath()
        rock.forEachIndexed { index, vertex ->
            when (index) {
                0 -> graphics.moveTo(vertex.x, vertex.y)
                else -> graphics.lineTo(vertex.x, vertex.y)
            }
        }
        graphics.closePath()
        graphics.stroke()

        graphics.restore()
    }

    private fun move(asteroid: Asteroid, canvas: Canvas) {
        asteroid.pos = warp(step(asteroid), Point2D(canvas.width, canvas.height))
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
