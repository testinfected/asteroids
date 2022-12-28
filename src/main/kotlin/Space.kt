import javafx.geometry.Bounds
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import kotlin.time.Duration.Companion.seconds

class Space(
    private val bounds: Bounds,
    inputs: Inputs,
) {
    private val asteroids = spawnAsteroids().toMutableList()
    private val missiles = mutableListOf<Missile>()
    private val splats = mutableListOf<Splat>()

    private val ship = Ship.spawnAt(pos = center, inputs = inputs)
        .also { ship ->  ship.signals += { handleEvent(it) } }

    private val scoreBoard = ScoreBoard.positionedAt(Vector(10.0, 50.0))

    private val center
        get() = bounds.center

    private fun spawnAsteroids(): List<Asteroid> {
        return (1..4).map { Asteroid.spawnAt(randomLocation(), 16.0) }
    }

    private fun randomLocation() = randomLocationWithin(bounds)

    fun update(now: Long) {
        updateShip(now)
        updateAsteroids(now)
        updateMissiles(now)
        findCollisions(now)
        updateSplats(now)
    }

    private fun updateShip(now: Long) {
        ship.update(now)
        ship.keepInBounds(bounds)
    }

    private fun updateAsteroids(now: Long) {
        for (asteroid in asteroids) {
            asteroid.update(now)
            asteroid.keepInBounds(bounds)
        }
    }

    private fun updateMissiles(now: Long) {
        for (missile in missiles.toTypedArray()) {
            missile.update(now)
            if (missile.shouldDie(now)) kill(missile)
        }
    }

    private fun findCollisions(now: Long) {
        asteroids.toList().forEach { asteroid ->
            checkMissileCollision(asteroid, now)
            checkShipCollision(asteroid, now)
        }
    }

    private fun checkMissileCollision(asteroid: Asteroid, now: Long) {
        missiles.toList().forEach { missile ->
            if (asteroid.isDead) return
            missile.checkCollisionWith(asteroid, now)
        }
    }

    private fun checkShipCollision(asteroid: Asteroid, now: Long) {
        if (asteroid.isDead) return
        ship.checkCollisionWith(asteroid, now)
    }

    private fun Missile.checkCollisionWith(asteroid: Asteroid, now: Long) {
        if (hits(asteroid)) {
            asteroid.explode(now)
            kill(this)
        }
    }

    private fun Ship.checkCollisionWith(asteroid: Asteroid, now: Long) {
        if (hits(asteroid)) {
            asteroid.explode(now)
            kill(this)
        }
    }

    private fun Asteroid.explode(now: Long) {
        val (parts, splat) = split(now)
        asteroids += parts
        splats += splat
        kill(this)
        updateScore(this)
    }

    private fun updateScore(asteroid: Asteroid) {
        scoreBoard.inc(asteroid.score())
    }

    private fun updateSplats(now: Long) {
        for (splat in splats.toTypedArray()) {
            splat.update(now)
            if (splat.shouldDie(now)) kill(splat)
        }
    }

    fun draw(stencil: Stencil) {
        clear(stencil)
        drawShip(stencil)
        drawAsteroids(stencil)
        drawMissiles(stencil)
        drawSplats(stencil)
        drawScoreBoard(stencil)
    }

    private fun clear(stencil: Stencil) = stencil {
        fill = grey
        fillRect(0.0, 0.0, bounds.width, bounds.height)
    }

    private fun drawShip(stencil: Stencil) {
        ship.draw(stencil)
    }

    private fun drawAsteroids(stencil: Stencil) {
        for (asteroid in asteroids) asteroid.draw(stencil)
    }

    private fun drawMissiles(stencil: Stencil) {
        for (missile in missiles) missile.draw(stencil)
    }

    private fun drawSplats(stencil: Stencil) {
        for (splat in splats) splat.draw(stencil)
    }

    private fun drawScoreBoard(stencil: Stencil) {
        scoreBoard.draw(stencil)
    }

    private fun handleEvent(event: GameEvent) {
        when (event) {
            is MissileFired -> born(event.missile)
        }
    }

    private fun kill(ship: Ship) {

    }

    private fun born(missile: Missile) {
        missiles += missile
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

    private val Asteroid.isDead
        get() = this !in asteroids

    companion object {
        val grey: Paint = Color.rgb(40, 40, 50)
    }
}


