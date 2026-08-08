package com.guide.core_api.guidecase

import com.fleeksoft.ksoup.Ksoup
import com.guide.core_api.Client
import com.guide.core_api.Resource
import com.guide.core_api.model.guide.GuideCategory
import com.guide.core_api.model.guide.GuideDetailCategory
import com.guide.core_api.model.guide.GuideSubCategory
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GuideCase {

    val baseUrl = "https://www.ifixit.com/"
    val client =  Client.createHttpClient()

    suspend fun getGuides(): Resource<Pair<List<GuideCategory>, List<GuideSubCategory>>> {
      return  try {
            val html = client.get(baseUrl +"guide").bodyAsText()
            val doc = Ksoup.parse(html)
            val body = doc.getElementsByClass("featured-categories")
            val list = body.select("a")
            val items = arrayListOf<GuideCategory>()
            list.forEach {
                val text = it.text()
                val image = it.select("img").attr("src")
                val url = it.attr("href")
                items.add(GuideCategory(
                    text, image, url
                ))
            }
          val subCategory = doc.getElementsByClass("sub-categories")
          val listSubCategory = subCategory.select("a")
          val itemSubCategory = arrayListOf<GuideSubCategory>()
          listSubCategory.forEach {
              val text = it.select("span.sub-category-title-text").text()
              val count = it.select("span.overflow-slide-in").text()
              val url = it.attr("href")
              itemSubCategory.add(
                  GuideSubCategory(
                      text , count, url
                  )
              )
          }

          Resource.Success(Pair(items, itemSubCategory) )
        } catch (e: Exception) {
            print("guideUsecase data ${e.cause}")
          Resource.Error(e.message ?: "")
        }
    }

    suspend fun getDetailCategory(url: String):Resource<GuideDetailCategory>{
        return try {
            val html = client.get(baseUrl +url.replaceFirst("^/".toRegex(), "")).bodyAsText()
            val doc = Ksoup.parse(html)
            val topContent = doc.getElementById("topContent")
            val subcategorySection = topContent?.getElementsByClass("subcategorySection")
            val categoryListCell = subcategorySection?.select("div.categoryListCell")
            val model = GuideDetailCategory()
            val listSub = arrayListOf<GuideCategory>()
            categoryListCell?.forEach {
                val text = it.select("a").text()
                val url = it.attr("href")
                val image = it.select("img").attr("src")
                listSub.add(GuideCategory(text, image, url))
            }
            model.listCategory = listSub
            Resource.Success(model)
        } catch (e: Exception) {
            print("guideUsecase data ${e.cause}")
            Resource.Error(e.message ?: "")
        }
    }

    suspend fun getDetailGuide(url: String) {
        client.get(baseUrl) {
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getData(){
        try {
            val html = client.get("https://en.wikipedia.org/").bodyAsText()
            val doc = Ksoup.parse(html)
            println("1. Mulai request")
            println("2. Request selesai")

            val title = doc
                .getElementById("mp-welcomecount")
                ?.selectFirst("h1")
                ?.text()

            println("3. Title = $title")

            println("guideUsecase data ")
        } catch (e: Exception) {
            print("guideUsecase data ${e.cause}")
            e.printStackTrace()
        }
    }

}