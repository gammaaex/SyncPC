import java.time.LocalTime
import java.time.temporal.ChronoUnit

class VirtualNTP {
    var myDateTime : LocalTime = LocalTime.now()
    private var clientSendTime : LocalTime
    var serverReceiveTime : LocalTime
    var serverSendTime : LocalTime
    private var clientReceiveTime : LocalTime
    private var roundTripTime: Long
    var offset : Long
    var offsetList = ArrayList<Long>()

    init {
        clientSendTime = myDateTime
        serverReceiveTime = myDateTime
        serverSendTime = myDateTime
        clientReceiveTime = myDateTime
        roundTripTime = 0
        offset = 0
    }

    private fun getNow() : LocalTime{
        return LocalTime.now()
    }

    fun clientSend(){
        clientSendTime = getNow()
    }

    fun serverReceive(){
        serverReceiveTime = getNow()
    }

    fun serverReceive(time : String?){
        serverReceiveTime = LocalTime.parse(time)
    }

    fun serverSend(){
        serverSendTime = getNow()
    }

    fun serverSend(time : String?){
        serverSendTime = LocalTime.parse(time)
    }

    fun clientReceive(){
        clientReceiveTime = getNow()
    }

    fun calcRoundTripTime(){
        roundTripTime = ChronoUnit.NANOS.between(clientSendTime,serverReceiveTime)
                            + ChronoUnit.NANOS.between(serverSendTime,clientReceiveTime)
    }

    fun calcOffset(){
        val a = ChronoUnit.NANOS.between(clientSendTime,serverReceiveTime)
        val b = ChronoUnit.NANOS.between(serverSendTime,clientReceiveTime)
        offset = (a - b) / 2
    }

    fun correctMyDateTime(){
        myDateTime = myDateTime.minusNanos(offset)
    }

    fun correctMyDateTime(nanoTime : Long){
        myDateTime = myDateTime.minusNanos(nanoTime)
    }

    fun getOffsetAverage() : Double{
        return offsetList.average()
    }
}