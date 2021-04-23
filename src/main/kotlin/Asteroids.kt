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
import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


typealias Vector = Point2D

class Ship(var pos: Vector, var angle: Double = 0.0) {
    var firing = false
}

class Missile(var pos: Vector, val velocity: Vector)

class Asteroid(
    var pos: Vector,
    var scale: Double,
    val velocity: Vector,
    val shape: Array<Vector>
)

class Splat(
    val pos: Vector,
    val born: Long,
    val angle: Double,
    val shape: Array<Vector>,
)

val splat = arrayOf(
    Vector(-2.0, 0.0),
    Vector(-2.0, -2.0),
    Vector(2.0, -2.0),
    Vector(3.0, 1.0),
    Vector(2.0, -1.0),
    Vector(0.0, 2.0),
    Vector(1.0, 3.0),
    Vector(-1.0, 3.0),
    Vector(-4.0, -1.0),
    Vector(-3.0, .0),
)

val rocks =
    arrayOf(
        arrayOf(
            Vector(0.0, -2.0),
            Vector(2.0, -4.0),
            Vector(4.0, -2.0),
            Vector(3.0, 0.0),
            Vector(4.0, 2.0),
            Vector(1.0, 4.0),
            Vector(-2.0, 4.0),
            Vector(-4.0, 2.0),
            Vector(-4.0, -2.0),
            Vector(-2.0, -4.0)
        ),
        arrayOf(
            Vector(2.0, -1.0),
            Vector(4.0, -2.0),
            Vector(2.0, -4.0),
            Vector(0.0, -3.0),
            Vector(-2.0, -4.0),
            Vector(-4.0, -2.0),
            Vector(-3.0, 0.0),
            Vector(-4.0, 2.0),
            Vector(-2.0, 4.0),
            Vector(-1.0, 3.0),
            Vector(2.0, 4.0),
            Vector(4.0, 1.0)
        ),
        arrayOf(
            Vector(-2.0, 0.0),
            Vector(-4.0, 1.0),
            Vector(-2.0, 4.0),
            Vector(0.0, 1.0),
            Vector(0.0, 4.0),
            Vector(2.0, 4.0),
            Vector(4.0, 1.0),
            Vector(4.0, -1.0),
            Vector(2.0, -4.0),
            Vector(-1.0, -4.0),
            Vector(-4.0, -1.0),
            Vector(-2.0, 0.0),
        ),
        arrayOf(
            Vector(1.0, 0.0),
            Vector(4.0, -1.0),
            Vector(4.0, -2.0),
            Vector(1.0, -4.0),
            Vector(-2.0, -4.0),
            Vector(-1.0, -2.0),
            Vector(-4.0, -2.0),
            Vector(-4.0, 1.0),
            Vector(-2.0, 4.0),
            Vector(1.0, 3.0),
            Vector(2.0, 4.0),
            Vector(4.0, 2.0),
            Vector(1.0, 0.0)
        )
    )

@ExperimentalTime
class Asteroids : Application() {

    private val grey: Paint = Color.rgb(40, 40, 50)

    private val inputs = arrayListOf<KeyCode>()

    val asteroids = mutableListOf<Asteroid>()
    val splats = mutableListOf<Splat>()
    val missiles = mutableListOf<Missile>()

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
                drawMissiles(canvas)
                handleInputs(ship)
                drawAsteroids(canvas)
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

    private fun handleInputs(ship: Ship) {
        if (KeyCode.RIGHT in inputs) ship.angle += 1
        if (KeyCode.LEFT in inputs) ship.angle -= 1
        if (KeyCode.SPACE in inputs && !ship.firing) fireMissile(ship)
        if (KeyCode.SPACE !in inputs) ship.firing = false
    }

    private fun fireMissile(ship: Ship) {
        ship.firing = true
        missiles += makeMissile(ship)
    }

    private fun makeShip(bounds: Bounds) = Ship(Vector(bounds.centerX, bounds.centerY))

    private fun makeMissile(ship: Ship) = Missile(
        ship.pos,
        Rotate(ship.angle).transform(Vector(2.0, 0.0))
    )

    private fun draw(ship: Ship, canvas: Canvas) = draw(ship, canvas.graphicsContext2D)

    private fun draw(ship: Ship, graphics: GraphicsContext) {
        graphics.save()

        graphics.apply {
            fill = Color.TRANSPARENT
            stroke = Color.WHITE
            lineWidth = 1.0
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

    private fun drawMissiles(canvas: Canvas) {
        for (missile in missiles) {
            drawMissile(missile, canvas)
            move(missile)
        }
    }

    private fun drawMissile(missile: Missile, canvas: Canvas) = drawMissile(missile, canvas.graphicsContext2D)

    private fun drawMissile(missile: Missile, graphics: GraphicsContext) {
        graphics.save()

        graphics.apply {
            fill = Color.WHITE
        }

        graphics.translate(missile.pos.x, missile.pos.y)
        graphics.fillOval(-1.0, -1.0, 2.0, 2.0)

        graphics.restore()
    }

    private fun move(missile: Missile) {
        missile.pos = missile.pos.add(missile.velocity)
    }

    private fun makeAsteroid(pos: Vector, scale: Double = 16.0) = Asteroid(
        pos = pos,
        scale,
        Rotate(Random.nextDouble(360.0)).transform(Vector(1.5, 0.0)),
        shape = rocks[Random.nextInt(4)]
    )

    private fun randomLocationOn(canvas: Canvas) = randomLocationWithin(canvas.layoutBounds)

    private fun randomLocationWithin(bounds: Bounds) =
        Vector(Random.nextDouble() * bounds.width + bounds.minX, Random.nextDouble() * bounds.height + bounds.minY)

    private fun drawAsteroids(canvas: Canvas) {
        for (asteroid in asteroids) {
            draw(asteroid, canvas)
            move(asteroid, canvas)
        }
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
        asteroid.pos = warp(step(asteroid), Vector(bounds.maxX, bounds.maxY))
    }

    private fun split(asteroid: Asteroid, now: Long) {
        if (Random.nextInt(until = 960) != 1) return
        if (asteroid.scale == 4.0) return

        val parts = (1..2).map {
            makeAsteroid(asteroid.pos, scale = asteroid.scale / 2)
        }

        asteroids -= asteroid
        asteroids += parts
        splats += Splat(asteroid.pos, born = now, shape = splat, angle = Random.nextDouble(360.0))
    }

    private fun drawSplats(now: Long, canvas: Canvas) {
        for (splat in splats.toTypedArray()) draw(splat, now, canvas)
    }

    private fun draw(splat: Splat, now: Long, canvas: Canvas): Unit = draw(splat, now, canvas.graphicsContext2D)

    private fun draw(splat: Splat, now: Long, graphics: GraphicsContext) {
        graphics.save()

        graphics.apply {
            fill = Color.WHITE
        }

        graphics.translate(splat.pos.x, splat.pos.y)
        graphics.scale(2.0, 2.0)
        graphics.rotate(splat.angle)

        val size = 1 + (now - splat.born).toDuration(DurationUnit.NANOSECONDS).inSeconds
        splat.shape.forEach { point ->
            graphics.fillOval(point.x * size, point.y * size, 2 / size, 2 / size)
        }

        if (size > 5) splats -= splat

        graphics.restore()
    }

    private fun step(asteroid: Asteroid): Vector {
        return asteroid.pos.add(asteroid.velocity)
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(Asteroids::class.java, *args)
        }
    }
}


fun warp(pos: Vector, bounds: Vector): Vector {
    return Vector(warp(pos.x, bounds.x), warp(pos.y, bounds.y))
}

fun warp(value: Double, bound: Double): Double {
    return (value + bound) % bound
}
