import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.net.InetAddress
import java.time.LocalTime


class Application : App() {
    override val primaryView = Start::class

    init {
        //reloadViewsOnFocus()
        Platform.setImplicitExit(true)
    }
}

class Start : View("SyncPC") {

    init {
        primaryStage.showingProperty().addListener({ observable, oldValue, newValue ->
            if (oldValue == true && newValue == false){
                println("exit")
                System.exit(0)
            }
        })
    }

    override val root = form {
        setPrefSize(500.0, 250.0)
        primaryStage.x = 0.0
        primaryStage.y = 0.0
        spacing = 10.0

        fieldset {
            field("Select Server or Client")
        }

        button("Server") {
            vgrow = Priority.ALWAYS
            useMaxSize = true
            action {
                println("Server Pressed!")
                replaceWith(ServerStarter::class)
            }
        }

        button("Client") {
            vgrow = Priority.ALWAYS
            useMaxSize = true
            action {
                println("Client Pressed")
                replaceWith(ClientStarter::class)
            }
        }

        addEventFilter(KeyEvent.KEY_PRESSED) {
//            var robot = Robot()
            var key = it.code
//            robot.keyPress(key.impl_getCode())
//            if (key == KeyCode.A) println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
            println("key_pressed : " + key + " " + key.impl_getCode())
//
//            val robot = FXRobotFactory.createRobot(scene)
//            robot.keyPress(key)
//
//
            it.consume()
        }
    }
}

class ServerStarter : View("SyncPC-Server") {
    val model = ViewModel()
    val address = InetAddress.getLocalHost()
    val ipAddress = address.hostAddress
    var portNumber = model.bind { SimpleStringProperty() }

    override val root = form {
        portNumber.value = "9999"

        fieldset {
            //vgrow = Priority.ALWAYS
            useMaxSize = true
            field("IP Address") {
                label(ipAddress)
            }
            field("Port Number") {
                textfield(portNumber).required()
            }
        }

        button("Start") {
            vgrow = Priority.ALWAYS
            useMaxSize = true
            action {
                replaceWith(ServerSetting::class)
            }
        }
    }
}

class ServerSetting : View("Server-Settings") {
    val serverStarter = find(ServerStarter::class)
    val server = Server(serverStarter.portNumber.value.toInt())
    val model = ViewModel()
    var timer = model.bind { SimpleStringProperty() }

    override val root = form {
        timer.value = "5"

        fieldset {
            field("IP Address") {
                label(serverStarter.ipAddress)
            }

            field("Port Number") {
                label(serverStarter.portNumber)
            }

            field("Start Media in Seconds") {
                textfield(timer).required()
            }
        }

        button("Set") {
            action {
                println("set timer")
                val now = LocalTime.now()
                val nowPlusTimer = now.plusSeconds(timer.value.toLong())
                server.send(nowPlusTimer.toString())
            }
        }
    }
}

class ClientStarter : View("SyncPC-Client") {
    val model = ViewModel()
    val ip1 = model.bind { SimpleStringProperty() }
    val ip2 = model.bind { SimpleStringProperty() }
    val ip3 = model.bind { SimpleStringProperty() }
    val ip4 = model.bind { SimpleStringProperty() }
    val portNumber = model.bind { SimpleStringProperty() }
    val moviePath = model.bind { SimpleStringProperty() }
    var filePath : String = ""
    var ipAddress : String = ""

    override val root = form {

        ip1.value = "192"
        ip2.value = "168"
        ip3.value = "10"
        ip4.value = "107"
        portNumber.value = "9999"

        fieldset {
            var selectedPath = textfield()
            selectedPath.hide()
            spacing = 10.0
            field("IP Address") {
                textfield(ip1).required()
                label(".")
                textfield(ip2).required()
                label(".")
                textfield(ip3).required()
                label(".")
                textfield(ip4).required()
            }
            field("Port Number") {
                textfield(portNumber).required()
            }

            button("Select Movie") {
                action {
                    //val filters = arrayOf(FileChooser.ExtensionFilter("Text Files", "*.txt"), FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"))
                    val filters = arrayOf(FileChooser.ExtensionFilter("All Files", "*.*"))
                    var files: List<File> = chooseFile("Select some text files", filters, FileChooserMode.Single)
                    println("The user chose $files")
                    selectedPath.text = files[0].toString()
                    filePath = selectedPath.text
                }
            }

            field("selected file") {
                selectedPath = textfield(moviePath)
                selectedPath.isEditable = false
                selectedPath.isDisable = true
                selectedPath.required()
            }

            button("Connect") {
                enableWhen { model.valid }
                model.commit {}
                action {
                    ipAddress = "${ip1.value}.${ip2.value}.${ip3.value}.${ip4.value}"
                    replaceWith(PlayerView::class)
                }
            }
        }
    }
}

/*
class Test :View(){ //https://qiita.com/mildtech/items/ca57a4cec356d59be278
    //tornadofx.Forms.ktにあるFormクラス。tornadofx.Formをインポート
    override val root = Form() //ViewクラスのrootプロパティをForm()で返される値でオーバーライド

    // ovaride val root = form{ //layoutの1つであるform builderを指定。fieldsetなどはformのフィールド

    //import tornadofx.fieldset：Forms.ktにあるFieldset()で初期化する、EventTargetを拡張した拡張関数
    //ほとんどの場合、トップレベル、すなわちパッケージ直下に拡張を定義する
    //そのような拡張を宣言しているパッケージの外で使用するには、それを呼び出し箇所でインポートする必要がある
    //拡張を定義すると、クラスに新たなメンバを挿入するのではなく、
    //そのクラスのインスタンスにおいて、ただ単にその新しい関数をただドット付きで呼べるようになるだけ。
    init {
        with(root){//スコープ関数with。root.を書かずにアクセス可能になる
            fieldset {//拡張関数fieldset。
                //actionやfieldsetはabstractメソッドなのでその場で中の処理を書く
            }
        }
    }
}
*/