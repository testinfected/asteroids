import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


@ExperimentalTime
class Game : Application() {

    private val inputs = arrayListOf<KeyCode>()

    val asteroids = mutableListOf<Asteroid>()
    val splats = mutableListOf<Splat>()
    val missiles = mutableListOf<Missile>()

    override fun start(stage: Stage) {
        stage.title = "Asteroids"

        val canvas = set(stage)
        val space = Space(bounds = canvas.layoutBounds)

        asteroids += (1..4).map { Asteroid.spawnAt(space.randomLocation(), 16.0) }

        val stencil = canvas.stencil

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                handleInputs(space.ship, now)
                space.clear(stencil)
                space.renderShip(stencil)
                drawAsteroids(stencil, space)
                drawMissiles(now, stencil)
                findCollisions(missiles, asteroids, now)
                drawSplats(now, stencil)
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

    private fun handleInputs(ship: Ship, now: Long) {
        if (KeyCode.RIGHT in inputs) ship.angle += 1
        if (KeyCode.LEFT in inputs) ship.angle -= 1
        if (KeyCode.SPACE in inputs && !ship.firing) fireMissile(ship, now)
        if (KeyCode.SPACE !in inputs) ship.firing = false
    }

    private fun fireMissile(ship: Ship, now: Long) {
        ship.firing = true
        missiles += makeMissile(ship, now)
    }

    private fun makeMissile(ship: Ship, now: Long) = Missile(
        ship.pos,
        Rotate(ship.angle).transform(v(2, 0)),
        born = now
    )

    private fun drawMissiles(now: Long, stencil: Stencil) {
        for (missile in missiles.toTypedArray()) {
            drawMissile(missile, now, stencil)
            move(missile)
        }
    }

    private fun drawMissile(missile: Missile, now: Long, stencil: Stencil) = stencil {
        fill = Color.WHITE
        translate(missile.pos.x, missile.pos.y)
        fillOval(-1.0, -1.0, 2.0, 2.0)

        restore()

        if ((now - missile.born).toDuration(DurationUnit.NANOSECONDS).inSeconds > 3) kill(missile)
    }

    private fun move(missile: Missile) {
        missile.pos = missile.pos.add(missile.velocity)
    }

    private fun drawAsteroids(stencil: Stencil, space: Space) {
        for (asteroid in asteroids) {
            draw(asteroid, stencil)
            moveAsteroid(asteroid, space)
        }
    }

    private fun draw(asteroid: Asteroid, stencil: Stencil) = stencil {
        fill = Color.TRANSPARENT
        stroke = Color.WHITE
        lineWidth = 1.0 / asteroid.scale

        translate(asteroid.pos.x, asteroid.pos.y)
        scale(asteroid.scale, asteroid.scale)

        beginPath()
        asteroid.shape.forEachIndexed { index, vertex ->
            when (index) {
                0 -> moveTo(vertex.x, vertex.y)
                else -> lineTo(vertex.x, vertex.y)
            }
        }
        closePath()
        stroke()
    }

    private fun moveAsteroid(asteroid: Asteroid, space: Space) = asteroid.update(space)

    private fun split(asteroid: Asteroid, now: Long) {
        makeSplat(asteroid.pos, now)
        kill(asteroid)

        if (asteroid.scale == 4.0) {
            return
        }

        val parts = (1..2).map {
            Asteroid.spawnAt(asteroid.pos, scale = asteroid.scale / 2)
        }
        asteroids += parts
    }

    private fun makeSplat(pos: Vector, now: Long) {
        splats += Splat(pos, born = now, shape = splat, angle = Random.nextDouble(360.0))
    }

    private fun drawSplats(now: Long, stencil: Stencil) {
        for (splat in splats.toTypedArray()) draw(splat, now, stencil)
    }

    private fun draw(splat: Splat, now: Long, stencil: Stencil) = stencil {
        fill = Color.WHITE

        translate(splat.pos.x, splat.pos.y)
        scale(2.0, 2.0)
        rotate(splat.angle)

        val size = 1 + (now - splat.born).toDuration(DurationUnit.NANOSECONDS).inSeconds
        splat.shape.forEach { point ->
            fillOval(point.x * size, point.y * size, 2 / size, 2 / size)
        }

        if (size > 5) kill(splat)

        restore()
    }

    private fun kill(asteroid: Asteroid) {
        asteroids -= asteroid
    }

    private fun kill(splat: Splat) {
        splats -= splat
    }

    private fun kill(missile: Missile) {
        missiles -= missile
    }

    private fun findCollisions(missiles: Collection<Missile>, asteroids: Collection<Asteroid>, now: Long) {
        val killDistance = 50.0
        missiles.zip(asteroids).forEach { (missile, asteroid) ->
            if (missile.pos.distance(asteroid.pos) < killDistance) {
                split(asteroid, now)
                kill(missile)
            }
        }
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(Game::class.java, *args)
        }
    }
}
