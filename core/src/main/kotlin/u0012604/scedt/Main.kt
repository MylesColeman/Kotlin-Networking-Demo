package u0012604.scedt

import kotlinx.coroutines.channels.Channel
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import u0012604.scedt.networking.NetworkHandler
import u0012604.scedt.screens.FirstScreen

class Main : KtxGame<KtxScreen>() {

    private val clientChannel = Channel<String>(10)
    private val serverChannel = Channel<String>(10)
    private val networkHandler by lazy {
        NetworkHandler(
            "127.0.0.1",
            3601,
            serverChannel,
            clientChannel
        )
    }

    override fun create() {
        KtxAsync.initiate()

        val screen =
            FirstScreen(
                networkHandler,
                clientChannel,
                serverChannel)

        addScreen(screen)

        setScreen<FirstScreen>()
    }

    override fun dispose() {
        super.dispose()

        serverChannel.close()
        clientChannel.close()
        networkHandler.dispose()
    }
}

