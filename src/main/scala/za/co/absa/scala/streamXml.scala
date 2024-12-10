package za.co.absa.scala

import org.apache.http.impl.client.{BasicCredentialsProvider, CloseableHttpClient, DefaultHttpClient, HttpClientBuilder, HttpClients}
import org.apache.http.auth.{AuthScope, NTCredentials}
import org.apache.http.client.methods.HttpGet
import scala.io.Source
import za.co.absa.scala.schemas.password
import za.co.absa.scala.schemas.username


case class ApiConfig(apiUrl: String, username: String, password: String)

object streamXml {
  def main(args: Array[String]): Unit = {


    val apiUrl = "xxxxxx"

    val apiConfig = ApiConfig(apiUrl: String, username: String, password: String)


    val credentialsProvider = new BasicCredentialsProvider()
    credentialsProvider.setCredentials(
      new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
      new NTCredentials(s"${apiConfig.username}:${apiConfig.password}"))


    val httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build()

    //val httpGet = new HttpGet(apiConfig.apiUrl)


    val request = new HttpGet(apiConfig.apiUrl)
    val response = httpClient.execute(request)

    println(response)

    val entity = response.getEntity
    val content = Source.fromInputStream(entity.getContent).mkString

    println(content)


    val xmlFilePath = "C:\\Users\\abmh712\\OneDrive - Absa\\Desktop\\cib\\output\\data.xml"
    scala.tools.nsc.io.File(xmlFilePath).writeAll(content)


    response.close()
    httpClient.close()
}}
