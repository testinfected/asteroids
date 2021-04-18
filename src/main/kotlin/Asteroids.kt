import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Rotate
import javafx.scene.transform.Transform
import javafx.stage.Stage
import kotlin.random.Random


class Asteroid(var pos: Point2D, var angle: Transform)


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

        val asteroids = (1..10).map { makeAsteroid(scene) }.toList()

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                clearScene()
                add(drawAsteroids(scene))
            }

            private fun drawAsteroids(scene: Scene): Node {
                val group = Group()
                for (asteroid in asteroids) {
                    move(asteroid, scene)
                    val shape = draw(asteroid)
                    group.children.add(shape)
                }
                return group
            }
        }

        timer.start()
        stage.show()
    }

    private fun set(stage: Stage): Pair<Group, Scene> {
        val graph = Group()
        val scene = Scene(graph, 800.0, 600.0, grey)
        stage.scene = scene
        return Pair(graph, scene)
    }

    private fun makeAsteroid(scene: Scene) = Asteroid(
        Point2D(Random.nextDouble() * scene.width, Random.nextDouble() * scene.height),
        Rotate(Random.nextDouble().times(360))
    )

    private fun draw(asteroid: Asteroid): Rectangle {
        return Rectangle(asteroid.pos.x, asteroid.pos.y, 60.0, 60.0).apply {
            fill = grey
            stroke = Color.WHITE
            strokeWidth = 2.0
        }
    }

    private fun move(asteroid: Asteroid, scene: Scene) {
        asteroid.pos = warp(step(asteroid), Point2D(scene.width, scene.height))
    }

    private fun step(asteroid: Asteroid): Point2D {
        val step = asteroid.angle.transform(velocity)
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
