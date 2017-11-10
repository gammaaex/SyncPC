class Server{
    private val serverListener : ServerListener
    private val sender = Sender()

    constructor() {
        serverListener = ServerListener()
        serverListener.start()
    }

    constructor(portNumber : Int){
        serverListener = ServerListener(portNumber)
        serverListener.start()
    }

    fun close(){
        serverListener
    }

    fun send (message : String){
        sender.send(message)
    }
}