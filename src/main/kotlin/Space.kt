import javafx.geometry.Bounds
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import kotlin.time.Duration.Companion.seconds

class Space(private val bounds: Bounds) {
    private val asteroids = spawnAsteroids().toMutableList()
    private val missiles = mutableListOf<Missile>()
    private val splats = mutableListOf<Splat>()

    private val ship: Ship = Ship.spawnAt(pos = center).also {
        it.listeners += object : ShipEventListener {
            override fun missileFired(missile: Missile) {
                missiles += missile
            }
        }
    }

    private val center
        get() = bounds.center

    private fun spawnAsteroids(): List<Asteroid> {
        return (1..4).map { Asteroid.spawnAt(randomLocation(), 16.0) }
    }

    private fun randomLocation() = randomLocationWithin(bounds)

    fun handleInputs(inputs: Inputs, now: Long) {
        ship.handleInputs(inputs, now)
    }

    fun update(now: Long) {
        updateAsteroids(now)
        updateMissiles(now)
        findCollisions(now)
        updateSplats(now)
    }

    private fun updateAsteroids(now: Long) {
        for (asteroid in asteroids) {
            asteroid.update(now)
            asteroid.wrap(bounds)
        }
    }

    private fun updateMissiles(now: Long) {
        for (missile in missiles.toTypedArray()) {
            missile.update(now)
            if (missile.olderThan(missileLifetime)) kill(missile)
        }
    }

    private fun findCollisions(now: Long) {
        missiles.toTypedArray().forEach missile@{ missile ->
            asteroids.forEach { asteroid ->
                if (missile.hits(asteroid)) {
                    val (parts, splat) = asteroid.split(now)
                    asteroids += parts
                    splats += splat
                    kill(asteroid)
                    kill(missile)
                    return@missile
                }
            }
        }
    }

    private fun updateSplats(now: Long) {
        for (splat in splats.toTypedArray()) {
            splat.update(now)
            if (splat.biggerThan(maxSplatSize)) kill(splat)
        }
    }

    fun draw(stencil: Stencil) {
        clear(stencil)
        drawShip(stencil)
        drawAsteroids(stencil)
        drawMissiles(stencil)
        drawSplats(stencil)
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

    private fun kill(asteroid: Asteroid) {
        asteroids -= asteroid
    }

    private fun kill(splat: Splat) {
        splats -= splat
    }

    private fun kill(missile: Missile) {
        missiles -= missile
    }

    companion object {
        const val maxSplatSize = 5.0
        val grey: Paint = Color.rgb(40, 40, 50)
        val missileLifetime = 3.seconds

    }
}


