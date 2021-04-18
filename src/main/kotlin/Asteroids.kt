import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import kotlin.random.Random

class Asteroids : Application() {

    private val grey = Color.rgb(40, 40, 50)

    val angle = Rotate(Random.nextDouble().times(360))
    val velocity = Point2D(1.5, 0.0)

    override fun start(stage: Stage) {
        stage.title = "Asteroids"

        val root = Group()
        val scene = Scene(root, 800.0, 600.0, grey)
        stage.scene = scene

        var pos = Point2D(200.0, 300.0)

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                root.children.clear()

                val step = angle.transform(velocity)
                pos = pos.add(step)
                if (pos.x > scene.width) pos = pos.subtract(scene.width, 0.0)
                if (pos.x < 0) pos = pos.add(scene.width, 0.0)
                if (pos.y > scene.height) pos = pos.subtract(0.0, scene.height)
                if (pos.y < 0) pos = pos.add(0.0, scene.height)

                val circle = Rectangle(pos.x, pos.y, 120.0, 120.0).apply {
                    fill = grey
                    stroke = Color.WHITE
                    strokeWidth = 2.0
                }

                root.children.add(circle)
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