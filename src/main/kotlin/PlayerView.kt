import javafx.geometry.Pos
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import tornadofx.*
import java.io.File


class PlayerView : View("Media Player") {
    private val clientStarter = find(ClientStarter::class)
    val path = clientStarter.filePath
    val file = File(path)
    val media = Media(file.toURI().toString())
    val mediaPlayer = MediaPlayer(media)
    val mediaView = MediaView(mediaPlayer)

    val client = Client(clientStarter.ipAddress,clientStarter.portNumber.value.toInt())


    init {
        primaryStage.showingProperty().addListener({ observable, oldParent, newParent ->
            if (newParent == true && oldParent == false){
                client.close()
            }
        })

//        mediaPlayer.setOnPlaying {
//            alert(Alert.AlertType.INFORMATION,
//                    "current time is ${Modal.currentTime}¥n" +
//                            "start time is ${Modal.startTime}¥n" +
//                            "offset time is ${Modal.offsetTime}¥n" +
//                            "correct time is ${Modal.correctTime}¥n"
//            )
//        }
    }

    override val root = vbox{

        style {
            backgroundColor += c("#000000")
        }
        alignment = Pos.CENTER

        add(mediaView)

        mediaView.fitWidthProperty().bind(widthProperty())
        mediaView.fitHeightProperty().bind(heightProperty())
    }
}