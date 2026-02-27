package u0012604.scedt.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import u0012604.scedt.ui.Hud
import u0012604.scedt.networking.NetworkHandler
import u0012604.scedt.entities.Bucket
import u0012604.scedt.entities.DropCollection

class FirstScreen(
    private val networkHandler: NetworkHandler,
    private val receiveChannel: ReceiveChannel<String>,
    private val sendChannel: SendChannel<String>
) : KtxScreen {

    private var music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3")).apply {
        isLooping = true
        volume = 0.5f
        play()
    }

    private val bgTexture = Texture("background.png")

    private val batch = SpriteBatch()
    private val viewport = FitViewport(8f, 5f)
    private val touchPos = Vector2()

    private var bucket = Bucket()

    private var rainDrops = DropCollection(viewport.worldWidth, viewport.worldHeight)

    private val hud = Hud(viewport)

    private var messageFromServer = "Waiting for server"
    private var waitingForServerTime = 0f

    private var dropsCaught = 0

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        coroutineScope.launch {
            while(true) {
                messageFromServer = receiveChannel.receive()

                Gdx.app.log("FirstScreen", messageFromServer)

                delay(250L)
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        if(networkHandler.isReady) {
            input()
            logic()
        }
        else {
            waitingForServerTime += delta
            messageFromServer = "Waiting for server${".".repeat(waitingForServerTime.toInt() % 4)}"
        }
        draw(delta)
    }

    private fun input() {
        if (Gdx.input.isTouched) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())

            viewport.unproject(touchPos)

            bucket.x = touchPos.x
        }
    }

    private fun logic() {
        val delta = Gdx.graphics.deltaTime

        rainDrops.logic(delta) {
            droplet, dropRectangle -> // todo: improve this interface!

            // Check for collisions with each raindrop and send a
            // message to the server
            if(bucket.collidedWith(dropRectangle)) {
                coroutineScope.launch {
                    sendChannel.send(
                        "Bucket and Drop Touched At ${"%.2f".format(droplet.x)}, ${
                            "%.2f".format(
                                droplet.y
                            )
                        }"
                    )
                }
                dropsCaught++
                droplet.isDead = true
            }
        }
    }

    private fun draw(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)

        batch.use {
            it.projectionMatrix = viewport.camera.combined

            it.draw(bgTexture, 0f, 0f, viewport.worldWidth, viewport.worldHeight)

            bucket.draw(it)

            rainDrops.draw(it)

            hud.textAt(it, 0f, 4.75f, messageFromServer)
            hud.textAt(it, 6.5f, 0.5f, "Drops: $dropsCaught")
        }
    }

    override fun dispose() {
        music.disposeSafely()
        bgTexture.disposeSafely()
        bucket.disposeSafely()
        hud.disposeSafely()
        batch.disposeSafely()
    }
}
