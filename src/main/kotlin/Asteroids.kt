import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import kotlin.random.Random


class Asteroid(var pos: Point2D, var angle: Double = 0.0)

class Ship(var pos: Point2D, var angle: Double = 0.0)


class Asteroids : Application() {

    private val grey: Paint = Color.rgb(40, 40, 50)
    private val velocity = Point2D(1.5, 0.0)

    override fun start(stage: Stage) {
        stage.title = "Asteroids"
        val (actors, scene) = set(stage)

        fun clearScene() {
            actors.children.clear()
        }

        fun add(actor: Node) {
            actors.children.add(actor)
        }

        val ship = makeShip(scene)
        val asteroids = (1..10).map { makeAsteroid(scene) }.toList()

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                clearScene()
                add(drawShip(scene))
                add(drawAsteroids(scene))
            }

            private fun drawAsteroids(scene: Scene): Node {
                val group = Group()
                for (asteroid in asteroids) {
                    val shape = draw(asteroid)
                    move(asteroid, scene)
                    group.children.add(shape)
                }
                return group
            }

            private fun drawShip(scene: Scene): Node {
                val shape = draw(ship)
                move(ship)
                return shape
            }
        }

        timer.start()
        stage.show()
    }

    private fun move(ship: Ship) {
        ship.angle += 1
    }

    private fun set(stage: Stage): Pair<Group, Scene> {
        val graph = Group()
        val scene = Scene(graph, 800.0, 600.0, grey)
        stage.scene = scene
        return Pair(graph, scene)
    }

    private fun makeShip(scene: Scene) = Ship(Point2D.ZERO.midpoint(Point2D(scene.width, scene.height)))

    private fun draw(ship: Ship): Polygon {
        val shape = Polygon(
            10.0, 0.0,
            -10.0, 6.0,
            -10.0, -6.0
        ).apply {
            layoutX = ship.pos.x
            layoutY = ship.pos.y
            fill = Color.TRANSPARENT
            stroke = Color.WHITE
            strokeWidth = 2.0
        }
        shape.transforms.add(Rotate(ship.angle))
        return shape
    }

    private fun makeAsteroid(scene: Scene) = Asteroid(
        Point2D(Random.nextDouble() * scene.width, Random.nextDouble() * scene.height),
        Random.nextDouble().times(360)
    )

    private fun draw(asteroid: Asteroid): Rectangle {
        return Rectangle(asteroid.pos.x - 30.0, asteroid.pos.y - 30.0, 60.0, 60.0).apply {
            fill = Color.TRANSPARENT
            stroke = Color.WHITE
            strokeWidth = 2.0
        }
    }

    private fun move(asteroid: Asteroid, scene: Scene) {
        asteroid.pos = warp(step(asteroid), Point2D(scene.width, scene.height))
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
