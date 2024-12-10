package za.co.absa.scala
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel

object read_xml {
  def main(args: Array[String]): Unit = {
    val filePath = args(0)
    val hdfsOutputPath = args(1)

    val spark = SparkSession.builder()
      .appName("Read XML from Local FS")
      .master("local[*]")
      .config("spark.jars.packages", "com.databricks:spark-xml_2.11:0.13.0")
      .getOrCreate()

    //val filePath = "C:\\Users\\abmh712\\OneDrive - Absa\\Desktop\\cib\\data.xml"
    //val addressPath = "/bigdatahdfs/project/slfc/tmp/recon/address_out"
    val clientPath = " "
    val classificationPath = " "
    val counterPartyIdPath = " "
    val countriesPath = " "
    val tacticalPath = "/bigdatahdfs/project/slfc/tmp/recon/tactical_out"

    //val filePath = "/home/abmh712/cib/Client_PRD_20241031.xml"
    //val filePath = "/bigdatahdfs/project/slfc/tmp/recon/client_xml/data.xml"
    //val hdfsOutputPath = "/bigdatahdfs/project/slfc/tmp/recon/client_out"
    //val hdfsOutputPath = "C:\\Users\\abmh712\\OneDrive - Absa\\Desktop\\cib\\output"


    val xmlDF: DataFrame = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "Data")
      .load(filePath)
      .persist(StorageLevel.MEMORY_AND_DISK)
      .cache()
      .repartition(500)


    // xmlDF.printSchema()


    val addressDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .withColumn("address", explode(col("Item.client.addresses.address")))
      .select(
        col("Item.client.suin").alias("suin"),
        col("address.country").alias("country"),
        col("address.line1").alias("address_line1"),
        col("address.line2").alias("address_line2"),
        col("address.suburb").alias("suburb_name"),
        col("address.town").alias("city_name"),
        col("address.type").alias("address_type"),
        col("address.postalCode").alias("postal_code"))

    val clientDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client._xmlns").alias("_xmlns"),
        col("Item.client._xsd").alias("_xsd"),
        col("Item.client._xsi").alias("_xsi"))

    val classificationsDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.classifications.classification.type").alias("classification_type"),
        col("Item.client.classifications.classification.value").alias("classification_value"))

    val counterpartyIdDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.counterpartyId.enterpriseId.id").alias("system_id"),
        col("Item.client.counterpartyId.enterpriseId.sourceSystem").alias("source_system"))

    val countriesDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.countries.country.type").alias("engagement_type"),
        col("Item.client.countries.country.value").alias("conformed_name")
      )

    val customerTypeCodeDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.customerTypeCode").alias("customerTypeCode"))

    val isDeletedDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.isDeleted").alias("isDeleted"))

    val legalNameDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.legalName").alias("organization_name"))

    val organizationalTypeDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.organizationalType").alias("organization_type"))

    val parentsDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.parents.parent.counterpartyId.id").alias("parent_id"),
        col("Item.client.parents.parent.counterpartyId.sourceSystem").alias("parent_sourcesystem"),
        col("Item.client.parents.parent.ownershipPercentage").alias("ownershippercentage")
      )

    val sbuIdDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.sbuId").alias("sbuId"),
        col("Item.client.sbuId.subSegmentId").alias("subSegmentId")
      )

    val suinDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.suin").alias("suin")
      )


    val tacticalDF = xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.suin").alias("suin"),
        col("Item.client.tactical.fica.date").alias("date"),
        col("Item.client.tactical.fica.expiryDate").alias("expiryDate"),
        col("Item.client.tactical.fica.status").alias("locking_status"),
        col("Item.client.tactical.registrations.registration.type").alias("type"),
        col("Item.client.tactical.registrations.registration.value").alias("value"),
        col("Item.client.organizationalType").alias("organization_type"),
        col("Item.client.legalName").alias("organization_name")

      )

    val typeDF= xmlDF
      .withColumn("Item", explode(col("Item")))
      .select(
        col("Item.client.type").alias("type")
      )

   // tacticalDF.show()



    addressDF.write
      .format("parquet")
      .mode("overwrite")
      .save(hdfsOutputPath)

    tacticalDF.write
      .format("parquet")
      .mode("overwrite")
    //  .save(tacticalPath)

    clientDF.write
      .format("parquet")
      .mode("overwrite")
    //  .save(clientPath)

    classificationsDF.write
      .format("parquet")
      .mode("overwrite")
   //   .save(classificationPath)

    counterpartyIdDF.write
      .format("parquet")
      .mode("overwrite")
   //   .save(counterPartyIdPath)

    countriesDF.write
      .format("parquet")
      .mode("overwrite")
   //   .save("countriesPath")

    customerTypeCodeDF
      .write
      .format("parquet")
      .mode("overwrite")
   //   .save("customerTypeCodePath")

    isDeletedDF
      .write
      .format("parquet")
      .mode("overwrite")
  //    .save("isDeletedDFPath")


    spark.stop()
  }
}
