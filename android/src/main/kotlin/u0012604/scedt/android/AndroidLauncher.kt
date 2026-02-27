package u0012604.scedt.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import u0012604.scedt.Main

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialize(Main(), AndroidApplicationConfiguration().apply {
            // Configure your application here.
            title = "Drop"

            useImmersiveMode = true // Recommended, but not required.
        })
    }
}
