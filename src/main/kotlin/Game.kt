import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import kotlin.time.ExperimentalTime


fun interface Inputs {
    operator fun contains(key: KeyCode): Boolean
}

class Keys : Inputs {
    private val keys = mutableSetOf<KeyCode>()

    override operator fun contains(key: KeyCode) = key in keys

    val pressed: EventHandler<in KeyEvent>
        get() = EventHandler { if (it.code !in keys) keys.add(it.code) }

    val released: EventHandler<in KeyEvent>
        get() = EventHandler { keys.remove(it.code) }
}

@ExperimentalTime
class Game : Application() {

    private val keys = Keys()

    override fun start(stage: Stage) {
        val canvas = set(stage)
        val space = Space(bounds = canvas.layoutBounds, inputs = keys)

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                space.update(now)
                space.draw(canvas.stencil)
            }
        }

        timer.start()
        stage.show()
    }

    private fun set(stage: Stage): Canvas {
        val canvas = Canvas(1344.0, 840.0)
        val scene = Scene(Group(canvas))
        stage.title = "Asteroids"
        stage.scene = scene
        scene.onKeyPressed = keys.pressed
        scene.onKeyReleased = keys.released

        return canvas
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(Game::class.java, *args)
        }
    }
}

