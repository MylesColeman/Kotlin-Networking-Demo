package u0012604.scedt.entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Disposable
import ktx.assets.disposeSafely

class Bucket() : Disposable {
    private val texture = Texture("bucket.png")
    private val sprite = Sprite(texture).apply {
        setSize(1f, 1f)
    }
    private var rectangle = Rectangle().apply {
        width = sprite.width
        height = sprite.height
    }

    var x: Float
        get() = sprite.x
        set(value: Float) {
            sprite.setCenterX(value)
            updateRectangle()
        }

    var y: Float
        get() = sprite.y
        set(value) {
            sprite.setCenterY(value)
            updateRectangle()
        }

    private fun updateRectangle() =
        rectangle.apply {
            x = sprite.x
            y = sprite.y
        }

    fun draw(batch: Batch) = sprite.draw(batch)

    fun collidedWith(otherRectangle: Rectangle) : Boolean = rectangle.overlaps(otherRectangle)

    override fun dispose() { texture.disposeSafely() }
}
