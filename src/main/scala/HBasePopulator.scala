import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Scan, Put, Connection}
import org.apache.hadoop.hbase.util.Bytes

/**
 * Created by ted.malaska on 11/12/15.
 */
object HBasePopulator {
  def main(args: Array[String]): Unit = {

  }

  def populate(numOfUsers:Int,
               numOfRecords:Int,
               waitTimeEvery1000:Int,
               connection: Connection,
                tableStr:String): Unit = {

    val bufferedMutator = connection.getBufferedMutator(new TableName(tableStr))
    val generator = new EnergyMonitorDataGen(numOfUsers)

    for (i <- 0 to numOfRecords) {

      val record = generator.next()

      val put = new Put(Bytes.toBytes(record.userId + "_" +
        (Long.MaxValue - record.time)))
      put.addColumn(HBaseContants.columnFamily,
        HBaseContants.column, Bytes.toBytes(record.usedAmount.toString))

      bufferedMutator.mutate(put)

      if (i % 1000 == 0) {
        Thread.sleep(waitTimeEvery1000)
      }
    }
    bufferedMutator.flush()
  }

  def megaScan(connection:Connection, tableStr:String): Unit = {
    val table = connection.getTable(new TableName(tableStr))
    val scan = new Scan()
    scan.setBatch(1000)
    scan.setCaching(1000)
    scan.setCacheBlocks(false)

    val scanner = table.getScanner(scan)

    val it = scanner.iterator()
    while(it.hasNext) {
      val result = it.next()
      println(" - " + Bytes.toString(result.getRow) + ":" +
        Bytes.toString(result.getValue(HBaseContants.columnFamily,
          HBaseContants.column)))
    }
  }
}
