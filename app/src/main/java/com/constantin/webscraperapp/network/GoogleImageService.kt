package com.constantin.webscraperapp.network

import org.jsoup.nodes.Document
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

// googles page size is 20
interface GoogleImageService {

    companion object {
        const val baseUrl = "https://www.google.com"
    }

    @GET("/search")
    suspend fun searchHtml(
        @Query("tbm") tbm: String? = null,
        @Query("q") search: String,
        @Query("start") start: Int = 0,
    ): Document

}

suspend fun GoogleImageService.getImageLinks(
    search: String,
    start: Int = 0,
): List<String> = searchHtml(
    tbm = "isch",
    search = search,
    start = start,
).getElementsByClass("t0fcAb").map { it.attr("src") }

suspend fun GoogleImageService.getWebLinks(
    search: String,
    start: Int = 0,
) = searchHtml(
    search = search,
    start = start,
).getElementsByClass("ZINbbc xpd O9g5cc uUPGi").map {
    WebSearch(
        title = it.getElementsByClass("BNeawe vvjwJb AP7Wnd").text(),
        description = it.getElementsByClass("BNeawe s3v9rd AP7Wnd").text(),
        url = it.getElementsByTag("a").attr("href").substringAfter("/url?q=").substringBefore("&sa"),
    )
}.filterNot { it.title == "" }

suspend fun <T : Any> GoogleImageService.fetch(request: suspend GoogleImageService.() -> T): Response<T> {
    return try {
        val response = request()
        Response.Success(response)
    } catch (e: HttpException) {
        when (val code = e.code()) {
            in Response.Failure.Fetch.clientErrorRange -> Response.Failure.Parse
            in Response.Failure.Fetch.serverErrorRange -> Response.Failure.Fetch(code)
            else -> Response.Failure.Fetch(code)
        }

    } catch (e: IOException) {
        Response.Error
    }
}