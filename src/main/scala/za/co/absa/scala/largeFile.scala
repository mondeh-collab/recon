package za.co.absa.scala

import org.apache.http.auth.{AuthScope, NTCredentials}
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.{BasicCredentialsProvider, HttpClientBuilder}
import org.slf4j.LoggerFactory
import za.co.absa.scala.schemas.{password, username}

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileOutputStream}


object largeFile {
  val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    val apiUrl = "http://client-svc.corp.dsarena.com:20111/api/Clients"
    val apiConfig = ApiConfig(apiUrl: String, username: String, password: String)
    val filePath = "C:\\Users\\abmh712\\OneDrive - Absa\\Desktop\\cib\\output\\data2.xml"

    val authProvider = new BasicCredentialsProvider()

    authProvider.setCredentials(
      new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
      new NTCredentials(s"${apiConfig.username}:${apiConfig.password}"))

    val httpClient = HttpClientBuilder.create()
      .setDefaultCredentialsProvider(authProvider)
      .build()

    val httpGet = new HttpGet(apiConfig.apiUrl)

    var bos: BufferedOutputStream = null
    var bis: BufferedInputStream = null

    try {
      val response = httpClient.execute(httpGet)
      val entity = response.getEntity

      if (entity != null) {
        logger.info(s"ContentType: ${entity.getContentType}")
        val contentLength = entity.getContentLength
        logger.info(s"Content Length: $contentLength")

        bis = new BufferedInputStream(entity.getContent)
        bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)))


        var inByte = bis.read()
        while (inByte != -1) {
          bos.write(inByte)
          inByte = bis.read()
        }

        logger.info("File downloaded successfully.")
      } else {
        logger.warn("Response entity is null. No file to download.")
      }
    } catch {
      case e: Exception =>
        logger.error("An error occurred while downloading the file.", e)
    } finally {
      if (bos != null) bos.close()
      if (bis != null) bis.close()
      httpClient.close()
    }
  }
}
