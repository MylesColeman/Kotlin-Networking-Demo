package u0012604.scedt.entities

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.Pool
import ktx.assets.disposeSafely

class Droplet : Pool.Poolable {
    var isDead = false
    lateinit var sprite: Sprite

    val x: Float
        get() = sprite.x

    val y: Float
        get() = sprite.y

    fun init(spr: Sprite) {
        sprite = spr
        sprite.setSize(DROP_WIDTH, DROP_HEIGHT)
    }

    override fun reset() {
        isDead = false
    }

    companion object {
        const val DROP_WIDTH = 1f
        const val DROP_HEIGHT = 1f
    }
}

class DropCollection(
    private val worldWidth: Float,
    private val worldHeight: Float
) : Disposable {
    private var dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"))
    private val dropTexture = Texture("drop.png")
    private val dropSprites = mutableListOf<Droplet>()
    private val dropletPool = object : Pool<Droplet>() {
        override fun newObject() = Droplet()
    }
    private var dropRectangle = Rectangle()
    private var dropTime = 0f

    fun add() = dropSprites.add(
        dropletPool.obtain().apply {
            val s = Sprite(dropTexture).apply {
                x = MathUtils.random(0f, worldWidth - Droplet.DROP_WIDTH)
                y = worldHeight
            }
            init(s)
        }
    )

    fun draw(batch: Batch) = dropSprites.forEach { it.sprite.draw(batch) }

    fun logic(
        delta: Float,
        additionalLogicPerDroplet: (Droplet, Rectangle) -> Unit = { _, _ -> }
    ) {
        with(dropSprites.iterator()) {
            // This is a different approach to remove the
            // drop elements when they go off screen.
            while (hasNext()) {
                val droplet = next()

                // change the Y position of the current droplet
                droplet.sprite.translateY(-2f * delta)

                dropRectangle.set(droplet.x, droplet.y, droplet.sprite.width, droplet.sprite.height)

                if (droplet.y < -droplet.sprite.height) {
                    remove()
                } else {
                    additionalLogicPerDroplet(droplet, dropRectangle)
                    if (droplet.isDead) {
                        remove()
                        dropSound.play()
                    }
                }
            }
        }

        dropTime += delta

        if(dropTime > 1f) {
            dropTime = 0f

            add()
        }
    }

    override fun dispose() {
        dropSound.disposeSafely()
        dropTexture.disposeSafely()
    }
}
