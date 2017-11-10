import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket

class ServerListener : Thread {

    private var portNumber: Int?

    private var serverSocket: ServerSocket?

    private var clientSocket: Socket? = null

    private var socketReader: BufferedReader? = null

    private var socketWriter: BufferedWriter? = null

    constructor() : this(9999)

    constructor(portNumber: Int) {
        this.portNumber = portNumber

        try {
            this.serverSocket = ServerSocket(portNumber)
        } catch (anException: IOException) {
            this.serverSocket = null
            System.err.printf("ポート番号「%d」のサーバーソケットを取得できません。\n", portNumber)
            System.err.printf("サーバープログラムを終了します。\n")
            System.exit(1)
        }

        System.out.printf("＠）ポート番号「%d」でサービスを開始しました。\n", portNumber)
        System.out.printf("＝）クライアントからの接続要求を待ちます。\n")

        println("end constructor : " + this.serverSocket)
    }

    override fun run(){
        println("start run : " + this.serverSocket)
        println("this is ServerListener")
        var virtualNtp = VirtualNTP()
        var message : String?

        try {
            while (true) {
                val clientSocket = this.serverSocket!!.accept()
                println("accept!")
                ClientSocketManager.clientSocketList.add(clientSocket)
                println("list add")

                repeat(100){
                    with(virtualNtp){
                        println("init read")
                        message = read(clientSocket)
                        if(message == "request") {
                            serverReceive()
                            println("serverReceive write")
                            write(clientSocket, serverReceiveTime.toString())

                            message = read(clientSocket)
                            if(message == "serverReceiveOk") {
                                serverSend()
                                println("serverSend write")
                                write(clientSocket, serverSendTime.toString())
                                println("end with")
                            }
                        }
                    }
                }
            }
        } catch (anException: IOException) {
            System.err.printf("ポート番号「%d」のサーバーソケットにエラーが発生しました。\n", this.portNumber)
            System.err.printf("サーバープログラムを終了します。\n")
            System.exit(1)
        } finally {
            println("ソケットを閉じます。")
            try {
                if (this.serverSocket != null) {
                    this.serverSocket!!.close()
                }
                if (this.clientSocket != null){
                    this.clientSocket!!.close()
                }
                if (this.socketReader != null) {
                    this.socketReader!!.close()
                }
                if (this.socketWriter != null){
                    this.socketWriter!!.close()
                }
            } catch (anException: IOException) {
                anException.printStackTrace()
            }
        }
    }

    private fun read(clientSocket: Socket): String? {
        var message: String? = null

        try {
            socketReader = BufferedReader(InputStreamReader(clientSocket.getInputStream(),"UTF-8"))
            message = socketReader!!.readLine()

            if (message == null) {
                throw IOException("予期せぬエラーです。")
            }

            System.out.printf("<<< %s\n", message)
        } catch (anException: IOException) {
            System.err.printf("サーバー「%d」と接続中にエラーが発生しました。\n", this.portNumber)
            System.err.printf("サーバー「%d」の稼働の状況を確認してください。\n", this.portNumber)
            if (this.socketReader != null) {
                this.socketReader!!.close()
            }
//            System.exit(1)
        }

        return message
    }

    private fun write(clientSocket : Socket,message : String){
        try {
            socketWriter = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"))

            socketWriter!!.write(message)
            socketWriter!!.newLine()
            socketWriter!!.flush()

            println(">>> " + message)
        } catch (anException: IOException) {
            System.err.printf("ポート番号「%d」のサーバーソケットにエラーが発生しました。\n", clientSocket.localPort)
            System.err.printf("サーバープログラムを終了します。\n")
            if (this.socketWriter != null) {
                this.socketWriter!!.close()
            }
//            System.exit(1)
        }
    }
}