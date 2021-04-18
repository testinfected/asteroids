import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage

class Asteroids : Application() {

    private val grey = Color.rgb(40, 40, 50)

    var x: Double = 200.0
    var y: Double = 300.0

    override fun start(stage: Stage) {
        stage.title = "Asteroids"

        val root = Group()
        val scene = Scene(root, 800.0, 600.0, grey)
        stage.scene = scene

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                root.children.clear()

                x++
                if (x > scene.width) x -= scene.width
                if (x < 0) x += scene.width
                y += 2
                if (y > scene.height) y -= scene.height
                if (y < 0) y+= scene.height

                val circle = Circle(x, y, 60.0).apply {
                    fill = grey
                    stroke = Color.WHITE
                    strokeWidth = 1.0
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