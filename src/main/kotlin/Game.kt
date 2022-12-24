import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import kotlin.time.ExperimentalTime


class Inputs {
    private val keys = mutableSetOf<KeyCode>()

    operator fun contains(key: KeyCode) = key in keys

    operator fun plusAssign(key: KeyCode) {
        if (key !in keys) keys.add(key)
    }

    operator fun minusAssign(key: KeyCode) {
        keys.remove(key)
    }
}

@ExperimentalTime
class Game : Application() {

    val inputs = Inputs()

    override fun start(stage: Stage) {
        val canvas = set(stage)
        val space = Space(bounds = canvas.layoutBounds)

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                space.handleInputs(inputs, now)
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
        scene.onKeyPressed = EventHandler { inputs += it.code }
        scene.onKeyReleased = EventHandler { inputs -= it.code }

        return canvas
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(Game::class.java, *args)
        }
    }
}

