package common

import javafx.application.Platform
import tornadofx.*


class Test : App() {
    override val primaryView = Main::class

    init {
        reloadViewsOnFocus()
        Platform.setImplicitExit(true)
    }
}

class Main : View() {

    override val root = form{
        label("1")
        label("2")
    }
}