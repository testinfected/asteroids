import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight


data class Score(
    private var value: Int,
) {
    operator fun plus(score: Score): Score {
        return Score(value + score.value)
    }

    fun toText(length: Int): String {
        return "$value".padStart(length, '0')
    }

    companion object {
        val ZERO = Score(0)
    }
}

class ScoreBoard(
    private val pos: Vector,
    private var score: Score = Score.ZERO,
) {
    fun inc(score: Score) {
        this.score += score
    }

    fun draw(stencil: Stencil) = stencil {
        font = Font.font("Monaco", FontWeight.BOLD, FontPosture.REGULAR, 50.0)
        fill = Color.WHITE
        fillText(score.toText(5), pos.x, pos.y)
    }

    companion object {
        fun positionedAt(pos: Vector) = ScoreBoard(pos)
    }
}