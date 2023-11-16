import javafx.geometry.Bounds
import javafx.scene.paint.Color
import javafx.scene.paint.Paint

class Space(
    private val bounds: Bounds,
    private val inputs: Inputs,
) {
    private val ships = mutableListOf<Ship>()
    private val asteroids = mutableListOf<Asteroid>()
    private val missiles = mutableListOf<Missile>()
    private val splats = mutableListOf<Splat>()

    private val scoreBoard = ScoreBoard.positionedAt(Vector(10.0, 50.0))

    private val center
        get() = bounds.center

    init {
        newAsteroidsWave()
    }

    private fun waveSize() = 4

    private fun randomLocation() = randomLocationWithin(bounds)

    private fun newAsteroidsWave() {
        asteroids.clear()
        repeat(waveSize()) { asteroids += Asteroid.spawnAt(randomLocation(), 16.0) }
    }

    private fun spawnShip() {
        ships.clear()
        ships += Ship.spawnAt(pos = center, inputs = inputs).apply { signals += { handleEvent(it) } }
    }

    fun start(now: Long) {
        newAsteroidsWave();
        spawnShip()
    }

    fun update(now: Long) {
        updateShip(now)
        updateAsteroids(now)
        updateMissiles(now)
        findCollisions(now)
        updateSplats(now)
    }

    private fun updateShip(now: Long) {
        ships.forEach {
            it.update(now)
            it.keepInBounds(bounds)
        }
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
        ships.toList().forEach {
            if (asteroid.isDead) return
            it.checkCollisionWith(asteroid, now)
        }
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
        ships.forEach { it.draw(stencil) }
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
        this.ships -= ship
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


