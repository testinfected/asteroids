import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import kotlin.time.ExperimentalTime


fun interface Inputs {
    operator fun contains(key: KeyCode): Boolean
}

class Keys : Inputs {
    private val keys = mutableSetOf<KeyCode>()

    override operator fun contains(key: KeyCode) = key in keys

    fun anyPressed(): Boolean = keys.isNotEmpty()

    fun clear() = keys.clear()

    val pressed: EventHandler<in KeyEvent>
        get() = EventHandler { if (it.code !in keys) keys.add(it.code) }

    val released: EventHandler<in KeyEvent>
        get() = EventHandler { keys.remove(it.code) }
}


sealed class GameEvent

data class MissileFired(val missile: Missile) : GameEvent()

typealias GameEventListener = (GameEvent) -> Unit

class Signaler {
    private val subscribers = mutableListOf<GameEventListener>()

    operator fun plusAssign(subscriber: GameEventListener) {
        subscribers += subscriber
    }

    fun announce(event: GameEvent) {
        subscribers.forEach { it(event) }
    }
}

class Invite(
    private val pos: Vector,
    private val size: Double,
) {
    fun draw(stencil: Stencil) = stencil {
        fill = Color.WHITE
        font = Font.font("Monaco", FontWeight.BOLD, FontPosture.REGULAR, size)
        fillText("Press any key to start", pos.x, pos.y)
    }
}

@ExperimentalTime
class Game : Application() {

    private val keys = Keys()
    private var started = false

    override fun start(stage: Stage) {
        val canvas = set(stage)
        val space = Space(bounds = canvas.layoutBounds, inputs = keys)
        val invite = Invite(canvas.layoutBounds.center - v(300, 0), 50.0)

        val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (shouldStart()) {
                    space.startGame(now)
                }
                space.update(now)
                space.draw(canvas.stencil)
                if (!started) {
                    invite.draw(canvas.stencil)
                }
            }
        }

        timer.start()
        stage.show()
    }

    private fun shouldStart(): Boolean {
        return !started && keys.anyPressed()
    }

    private fun Space.startGame(now: Long) {
        started = true
        keys.clear()
        start(now)
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

