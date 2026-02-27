package u0012604.scedt.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.assets.disposeSafely

// Very simple HUD class...
// Should use Stage2D...
class Hud(viewport: Viewport) : Disposable {
    private val hudFont = BitmapFont().apply {
        color = Color.RED
        setUseIntegerPositions(false)
        data.setScale(viewport.worldHeight / Gdx.graphics.height * 4f)
    }

    fun textAt(batch: Batch, x: Float, y: Float, text: String) {
        hudFont.draw(batch, text, x, y)
    }

    override fun dispose() {
        hudFont.disposeSafely()
    }
}
