import tornadofx.*
import java.awt.Robot
import java.awt.event.KeyEvent
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.net.UnknownHostException
import java.time.LocalTime

class Receiver : Thread {

    private var hostString: String?

    private var portNumber: Int?

    private var clientSocket: Socket?

    private var socketReader: BufferedReader?

    private var socketWriter: BufferedWriter?

    private var correctTime: Long = 0

    constructor() : this("localhost", 9999)

    constructor(hostString: String, portNumber: Int) {
        this.hostString = hostString
        this.portNumber = portNumber

        try {
            this.clientSocket = Socket(hostString, portNumber)
            this.socketReader = BufferedReader(InputStreamReader(this.clientSocket!!.getInputStream(), "UTF-8"))
            this.socketWriter = BufferedWriter(OutputStreamWriter(this.clientSocket!!.getOutputStream(), "UTF-8"))
        } catch (anException: UnknownHostException) {
            this.clientSocket = null
            this.socketReader = null
            this.socketWriter = null
            System.err.printf("サーバー「%s:%d」が不明です。\n", hostString, portNumber)
            System.err.printf("サーバー「%s:%d」のアドレスとポート番号を確かめてください。\n", hostString, portNumber)
            System.exit(1)
        } catch (anException: IOException) {
            this.clientSocket = null
            this.socketReader = null
            this.socketWriter = null
            System.err.printf("サーバー「%s:%d」の接続を確立できません。\n", hostString, portNumber)
            System.err.printf("サーバー「%s:%d」が動いていることを確かめてください。\n", hostString, portNumber)
            System.exit(1)
        }
    }

    override fun run() {
        var message: String?
        val playerView = find(PlayerView::class)

        System.out.printf("＊）サーバー「%s:%d」に接続できました。\n", this.hostString, this.portNumber)
        println("this is Receiver")

        correctTime = initialize()

        try {
            while (true) {
                message = read()
                var correctNow: LocalTime
                var timer = LocalTime.parse(message)
                var now = LocalTime.now()
                println("current time is " + now)
                println("start time is $timer")
                println("offset time is $correctTime")
                println("correct time is ${timer.minusNanos(correctTime)}")

                Modal.currentTime = now.toString()
                Modal.startTime = timer.toString()
                Modal.offsetTime = correctTime.toString()
                Modal.correctTime = timer.minusNanos(correctTime).toString()

                while (true) {
                    correctNow = LocalTime.now()
                    if (correctNow.isAfter(timer.minusNanos(correctTime))) {
                        println("media start!! : ${LocalTime.now()}")
                        playerView.mediaPlayer.play()

                        var robot = Robot()
                        robot.keyPress(KeyEvent.VK_SPACE)
                        while (true) {

                        }
                    }
                }
            }
        } finally {
            close()
        }
    }

    fun close() {
        try {
            if (this.clientSocket != null) {
                this.clientSocket!!.close()
            }
            if (this.socketReader != null) {
                this.socketReader!!.close()
            }
            if (this.socketWriter != null) {
                this.socketWriter!!.close()
            }
        } catch (anException: IOException) {
            anException.printStackTrace()
        }
    }

    private fun initialize(): Long {
        var virtualNtp = VirtualNTP()
        repeat(100) {
            with(virtualNtp) {
                clientSend()
                write("request")
                println("serverReceive")
                serverReceive(read())

                write("serverReceiveOk")
                println("serverSend")
                serverSend(read())

                println("clientReceive")
                clientReceive()

                calcOffset()
                println("offset is $offset")
                offsetList.add(offset)
            }
        }

        return virtualNtp.getOffsetAverage().toLong()
    }

    private fun read(): String? {
        var message: String? = null
        try {
            message = socketReader!!.readLine()

            if (message == null) {
                throw IOException("予期せぬエラーです。")
            }

            System.out.printf("<<< %s\n", message)
        } catch (anException: IOException) {
            System.err.printf("サーバー「%s:%d」と接続中にエラーが発生しました。\n", this.hostString, this.portNumber)
            System.err.printf("サーバー「%s:%d」の稼働の状況を確認してください。\n", this.hostString, this.portNumber)
            System.exit(1)
        }

        return message
    }

    private fun write(message: String) {
        try {

            socketWriter!!.write(message)
            socketWriter!!.newLine()
            socketWriter!!.flush()

            println(">>> " + message)
        } catch (anException: IOException) {
            System.err.printf("サーバー「%s:%d」と接続中にエラーが発生しました。\n", this.hostString, this.portNumber)
            System.err.printf("サーバー「%s:%d」の稼働の状況を確認してください。\n", this.hostString, this.portNumber)
            System.exit(1)
        }
    }
}

object Modal {
    var currentTime : String = ""
    var startTime : String = ""
    var offsetTime : String = ""
    var correctTime : String = ""
}