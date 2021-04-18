import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Rotate
import javafx.scene.transform.Transform
import javafx.stage.Stage
import kotlin.random.Random


class Asteroid(var pos: Point2D, var angle: Transform)


class Asteroids : Application() {

    private val grey = Color.rgb(40, 40, 50)

    private val velocity = Point2D(1.5, 0.0)

    override fun start(stage: Stage) {
        stage.title = "Asteroids"

        val root = Group()
        val scene = Scene(root, 800.0, 600.0, grey)
        stage.scene = scene

        val asteroids = (1..5).map {
            Asteroid(
                Point2D(Random.nextDouble() * scene.width, Random.nextDouble() * scene.height),
                Rotate(Random.nextDouble().times(360))
            )
        }.toList()

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                root.children.clear()

                for (asteroid in asteroids) {
                    val step = asteroid.angle.transform(velocity)
                    asteroid.pos = asteroid.pos.add(step)
                    if (asteroid.pos.x > scene.width) asteroid.pos = asteroid.pos.subtract(scene.width, 0.0)
                    if (asteroid.pos.x < 0) asteroid.pos = asteroid.pos.add(scene.width, 0.0)
                    if (asteroid.pos.y > scene.height) asteroid.pos = asteroid.pos.subtract(0.0, scene.height)
                    if (asteroid.pos.y < 0) asteroid.pos = asteroid.pos.add(0.0, scene.height)

                    val shape = Rectangle(asteroid.pos.x, asteroid.pos.y, 120.0, 120.0).apply {
                        fill = grey
                        stroke = Color.WHITE
                        strokeWidth = 2.0
                    }

                    root.children.add(shape)
                }
            }
        }

        timer.start()
        stage.show()
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(Asteroids::class.java, *args)
        }
    }
}