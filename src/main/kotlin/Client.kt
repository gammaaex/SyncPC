
/**
 * エコークライアントのプログラムです。
 */
class Client{
    var receiver : Receiver

    /**
     * エコークライアントのコンストラクタです。
     */
    constructor(){
        receiver = Receiver()
        receiver.start()
    }

    /**
     * エコーサーバーのアドレスとポート番号を指定できるエコークライアントのコンストラクタです。
     * @param hostString サーバーのホスト名
     * @param portNumber ポート番号
     */
    constructor(hostString: String, portNumber: Int) {
        receiver = Receiver(hostString,portNumber)
        receiver.start()
    }

    fun close(){
        receiver.close()
    }

    /**
     * エコークライアントが行う処理です。
     */
    fun perform() {

    }
}