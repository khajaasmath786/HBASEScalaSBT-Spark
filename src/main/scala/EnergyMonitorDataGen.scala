import java.util.Random

class EnergyMonitorDataGen(numOfUsers:Int)  {

  val r = new Random()

  def next(): EnergyMonitorRecord = {
    new EnergyMonitorRecord(r.nextInt(numOfUsers), r.nextLong(), System.currentTimeMillis())
  }
}
