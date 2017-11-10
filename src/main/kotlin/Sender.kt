import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.IOException

/**
 * エコーサーバーのスレッドプログラム：この例題を改変して大きなプログラムを作る足がかりにしてください。
 */
class Sender {
    fun send(message : String) {
        ClientSocketManager.clientSocketList.forEach {
            try {
                val socketWriter = BufferedWriter(OutputStreamWriter(it.getOutputStream(), "UTF-8"))
                socketWriter.write(message)
                socketWriter.newLine()
                socketWriter.flush()

                // リソース（リーダー・ライター・ソケット）を閉じます。
                socketWriter.close()
                it.close()
            } catch (anException: IOException) {
                System.err.printf("ポート番号「%d」のサーバーソケットにエラーが発生しました。\n", it.localPort)
                System.err.printf("サーバープログラムを終了します。\n")
                System.exit(1)
            }
        }

    }
}