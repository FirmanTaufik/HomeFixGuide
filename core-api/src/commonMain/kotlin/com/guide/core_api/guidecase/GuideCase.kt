package com.guide.core_api.guidecase

import com.fleeksoft.ksoup.Ksoup
import com.guide.core_api.Client
import com.guide.core_api.Resource
import com.guide.core_api.model.guide.GuideCategory
import com.guide.core_api.model.guide.GuideDetailCategory
import com.guide.core_api.model.guide.GuideStep
import com.guide.core_api.model.guide.GuideSubCategory
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class GuideCase {

    val baseUrl = "https://www.ifixit.com/"
    val client =  Client.createHttpClient()

    suspend fun getGuides(): Pair<List<GuideCategory>, List<GuideSubCategory>> {
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

        return Pair(items, itemSubCategory)
    }

    suspend fun getDetailCategory(url: String): GuideDetailCategory {
        val targetUrl = if (url.startsWith("http")) url else baseUrl + url.replaceFirst("^/".toRegex(), "")
        val html = client.get(targetUrl).bodyAsText()
        val doc = Ksoup.parse(html)
        
        val pageTitle = doc.selectFirst("h1")?.text() ?: ""
        val listSub = arrayListOf<GuideCategory>()
        val listGuides = arrayListOf<GuideCategory>()
        val guideSteps = arrayListOf<GuideStep>()
        var summaryText = ""

        // 1. Selector untuk subcategory / device / product types di iFixit
        val subcatElements = doc.select(".categoryListCell, .subcategorySection .categoryListCell, .subcategories a, .device-grid a, .device-card a, .device-list a, .category-grid a, a.category-card")
        subcatElements.forEach { el ->
            val link = if (el.tagName() == "a") el else el.selectFirst("a")
            val itemUrl = link?.attr("href") ?: el.attr("href")
            val text = link?.text()?.ifEmpty { el.text() } ?: el.text()
            val img = el.selectFirst("img")
            val image = img?.attr("src")?.ifEmpty { img.attr("data-src") } ?: ""
            if (text.isNotBlank() && itemUrl.isNotBlank() && !itemUrl.startsWith("#") && !itemUrl.contains("javascript:")) {
                if (!itemUrl.contains("/Guide/", ignoreCase = true) && !isIgnoredLink(text, itemUrl)) {
                    listSub.add(GuideCategory(text = text.trim(), image = image, url = itemUrl))
                }
            }
        }

        // 2. Selector untuk daftar panduan perbaikan (Repair Guides)
        val guideElements = doc.select(".guideListCell, .guide-item, .guide-card, .device-guides a, .guideContainer a, a.guide-title, #guides a, .guides-list a, div.guide, div.topic-guide, a[href*='/Guide/']")
        guideElements.forEach { el ->
            val link = if (el.tagName() == "a") el else el.selectFirst("a")
            val itemUrl = link?.attr("href") ?: el.attr("href")
            val text = link?.text()?.ifEmpty { el.text() } ?: el.text()
            val img = el.selectFirst("img")
            val image = img?.attr("src")?.ifEmpty { img.attr("data-src") } ?: ""
            if (text.isNotBlank() && itemUrl.isNotBlank() && !itemUrl.startsWith("#") && !itemUrl.contains("javascript:")) {
                if ((itemUrl.contains("/Guide/", ignoreCase = true) || itemUrl.contains("/Wiki/", ignoreCase = true)) && !isIgnoredLink(text, itemUrl)) {
                    listGuides.add(GuideCategory(text = text.trim(), image = image, url = itemUrl))
                }
            }
        }

        // 3. Deteksi Step-by-Step Guide HANYA jika halaman memang merupakan halaman panduan spesifik
        val isExplicitGuideUrl = targetUrl.contains("/Guide/", ignoreCase = true) || targetUrl.contains("/Wiki/", ignoreCase = true)
        if (isExplicitGuideUrl || (listSub.isEmpty() && listGuides.isEmpty())) {
            val stepContainers = doc.select(".step, .step-container, div[id^=step_], .guide-step")
            if (stepContainers.isNotEmpty()) {
                var stepNum = 1
                stepContainers.forEach { stepEl ->
                    val stepTitle = stepEl.selectFirst(".step-title, h3, h4")?.text() ?: "Langkah $stepNum"
                    val lines = stepEl.select(".step-instruction, .step-instructions li, .step-text, .step-body p, p").mapNotNull { p ->
                        val t = p.text().trim()
                        if (t.isNotBlank()) t else null
                    }
                    val images = stepEl.select("img").mapNotNull { img ->
                        val src = img.attr("src").ifEmpty { img.attr("data-src") }
                        if (src.isNotBlank()) src else null
                    }
                    if (lines.isNotEmpty() || images.isNotEmpty()) {
                        guideSteps.add(
                            GuideStep(
                                stepNumber = stepNum++,
                                title = stepTitle,
                                lines = lines,
                                images = images
                            )
                        )
                    }
                }
            }

            // 4. Deteksi Wiki / Troubleshooting Article
            val wikiBody = doc.selectFirst("#wiki-body, .wikiSection, .article-body, #mainContent, #main, .wiki-text")
            if (guideSteps.isEmpty() && wikiBody != null && isExplicitGuideUrl) {
                summaryText = wikiBody.select("p").take(3).joinToString("\n\n") { it.text().trim() }
                val headings = wikiBody.select("h2, h3")
                var sectionNum = 1
                headings.forEach { heading ->
                    val headingTitle = heading.text().trim()
                    if (headingTitle.isNotBlank() && !headingTitle.equals("Comments", ignoreCase = true) && !headingTitle.equals("Contribute", ignoreCase = true)) {
                        val sectionLines = arrayListOf<String>()
                        var sibling = heading.nextElementSibling()
                        while (sibling != null && !sibling.tagName().startsWith("h", ignoreCase = true)) {
                            if (sibling.tagName() == "ul" || sibling.tagName() == "ol") {
                                sibling.select("li").forEach { li ->
                                    if (li.text().isNotBlank()) sectionLines.add("• " + li.text().trim())
                                }
                            } else if (sibling.tagName() == "p") {
                                if (sibling.text().isNotBlank()) sectionLines.add(sibling.text().trim())
                            }
                            sibling = sibling.nextElementSibling()
                        }
                        if (sectionLines.isNotEmpty()) {
                            guideSteps.add(
                                GuideStep(
                                    stepNumber = sectionNum++,
                                    title = headingTitle,
                                    lines = sectionLines,
                                    images = emptyList()
                                )
                            )
                        }
                    }
                }
            }
        }

        // 5. Fallback link jika listSub dan listGuides masih kosong
        if (listSub.isEmpty() && listGuides.isEmpty() && guideSteps.isEmpty()) {
            val fallbackElements = doc.select("#topContent a, .category-list a, .subcategorySection a, #mainContent a")
            fallbackElements.forEach { el ->
                val text = el.text().trim()
                val itemUrl = el.attr("href")
                val img = el.selectFirst("img")
                val image = img?.attr("src")?.ifEmpty { img.attr("data-src") } ?: ""
                if (text.isNotBlank() && itemUrl.isNotBlank() && !itemUrl.startsWith("#") && !itemUrl.contains("javascript:") && text.length > 2 && !isIgnoredLink(text, itemUrl)) {
                    if (itemUrl.contains("/Guide/") || itemUrl.contains("/Wiki/")) {
                        listGuides.add(GuideCategory(text = text, image = image, url = itemUrl))
                    } else if (itemUrl.contains("/Device/") || itemUrl.contains("/Category/")) {
                        listSub.add(GuideCategory(text = text, image = image, url = itemUrl))
                    }
                }
            }
        }

        val isStepGuidePage = isExplicitGuideUrl && (guideSteps.isNotEmpty() || (listSub.isEmpty() && listGuides.isEmpty()))

        // Extract iFixit Metadata
        val breadcrumbs = doc.select(".breadcrumbs a, .breadcrumb a, nav.breadcrumbs a").map { it.text().trim() }.filter { it.isNotBlank() }
        val introText = doc.selectFirst(".guide-intro, .device-description, #deviceDescription, .wiki-intro, p.lead")?.text()?.trim() ?: ""
        val authorText = doc.selectFirst(".author-name, .author .name, .guide-author a, .user-name")?.text()?.trim() ?: ""
        val difficultyText = doc.selectFirst(".difficulty .value, .difficulty, [class*='difficulty']")?.text()?.replace("Difficulty", "", ignoreCase = true)?.trim() ?: ""
        val timeRequiredText = doc.selectFirst(".time_required .value, .time_required, [class*='time_required']")?.text()?.replace("Time Required", "", ignoreCase = true)?.trim() ?: ""
        val toolsList = doc.select(".guide-tools a, .tools-list a, .tools-container a, div.tools li, div.tools a").map { it.text().trim() }.filter { it.isNotBlank() && !it.startsWith("Buy", ignoreCase = true) }.distinct()
        val partsList = doc.select(".guide-parts a, .parts-list a, .parts-container a, div.parts li, div.parts a").map { it.text().trim() }.filter { it.isNotBlank() && !it.startsWith("Buy", ignoreCase = true) }.distinct()

        val filteredSub = listSub.filter { !isIgnoredLink(it.text, it.url) }.distinctBy { it.url }
        val filteredGuides = listGuides.filter { !isIgnoredLink(it.text, it.url) }.distinctBy { it.url }

        return GuideDetailCategory(
            title = pageTitle,
            introduction = introText,
            author = authorText,
            difficulty = difficultyText,
            timeRequired = timeRequiredText,
            tools = toolsList,
            parts = partsList,
            breadcrumbs = breadcrumbs,
            listCategory = filteredSub,
            listGuides = filteredGuides,
            contentSummary = summaryText,
            steps = guideSteps,
            isStepGuide = isStepGuidePage
        )
    }

    private fun isIgnoredLink(text: String, url: String): Boolean {
        val lowerText = text.lowercase().trim()
        val lowerUrl = url.lowercase().trim()
        return lowerText.contains("create a guide") ||
                lowerText.contains("create a new guide") ||
                lowerText.contains("add a guide") ||
                lowerText.contains("contribute") ||
                lowerText.contains("edit this page") ||
                lowerText.contains("ask a question") ||
                lowerUrl.endsWith("/new") ||
                lowerUrl.contains("/guide/new") ||
                lowerUrl.contains("/guide/create") ||
                lowerUrl.contains("/answers/ask") ||
                lowerUrl.contains("/answers/new") ||
                lowerUrl.contains("/user/") ||
                lowerUrl.contains("/login") ||
                lowerUrl.contains("/register") ||
                lowerUrl.contains("/store") ||
                lowerUrl.contains("/cart")
    }

    suspend fun searchGuides(query: String): List<GuideCategory> {
        if (query.isBlank()) return emptyList()
        val results = arrayListOf<GuideCategory>()
        val encodedQuery = query.trim().replace(" ", "+")

        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        // 1. Coba Search via iFixit REST API 2.0 (search endpoint)
        try {
            val apiUrl = "https://www.ifixit.com/api/2.0/search/$encodedQuery?limit=30"
            val jsonText = client.get(apiUrl).bodyAsText()
            val rootObj = json.parseToJsonElement(jsonText).jsonObject
            val resultsArray = rootObj["results"]?.jsonArray
            resultsArray?.forEach { itemEl ->
                val itemObj = itemEl.jsonObject
                val title = itemObj["title"]?.jsonPrimitive?.content ?: ""
                val itemUrl = itemObj["url"]?.jsonPrimitive?.content ?: ""
                val dataType = itemObj["dataType"]?.jsonPrimitive?.content ?: ""

                var imgUrl = ""
                val imageObj = itemObj["image"]?.jsonObject
                if (imageObj != null) {
                    imgUrl = imageObj["thumbnail"]?.jsonPrimitive?.content
                        ?: imageObj["standard"]?.jsonPrimitive?.content
                        ?: imageObj["medium"]?.jsonPrimitive?.content ?: ""
                }

                val finalUrl = when {
                    itemUrl.isNotBlank() -> itemUrl
                    dataType.equals("category", ignoreCase = true) -> "/Device/${title.replace(" ", "_")}"
                    dataType.equals("guide", ignoreCase = true) -> "/Guide/${title.replace(" ", "+")}"
                    else -> ""
                }

                if (title.isNotBlank() && finalUrl.isNotBlank() && !isIgnoredLink(title, finalUrl)) {
                    results.add(
                        GuideCategory(
                            text = title,
                            image = imgUrl,
                            url = finalUrl
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Jika API search kosong, coba API suggest
        if (results.isEmpty()) {
            try {
                val suggestUrl = "https://www.ifixit.com/api/2.0/suggest/$encodedQuery?do=search"
                val jsonText = client.get(suggestUrl).bodyAsText()
                val rootObj = json.parseToJsonElement(jsonText).jsonObject
                val resultsArray = rootObj["results"]?.jsonArray
                resultsArray?.forEach { itemEl ->
                    val itemObj = itemEl.jsonObject
                    val title = itemObj["title"]?.jsonPrimitive?.content ?: ""
                    val itemUrl = itemObj["url"]?.jsonPrimitive?.content ?: ""
                    val imageObj = itemObj["image"]?.jsonObject
                    val imgUrl = imageObj?.get("thumbnail")?.jsonPrimitive?.content
                        ?: imageObj?.get("standard")?.jsonPrimitive?.content ?: ""

                    if (title.isNotBlank() && itemUrl.isNotBlank() && !isIgnoredLink(title, itemUrl)) {
                        results.add(
                            GuideCategory(
                                text = title,
                                image = imgUrl,
                                url = itemUrl
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 3. Fallback ke HTML scraping jika kedua API tidak mengembalikan hasil
        if (results.isEmpty()) {
            try {
                val searchUrl = baseUrl + "Search?query=" + encodedQuery
                val html = client.get(searchUrl).bodyAsText()
                val doc = Ksoup.parse(html)
                val searchElements = doc.select(".search-result, .searchResult, .result-item, .search-card, a[href*='/Device/'], a[href*='/Guide/'], a[href*='/Wiki/']")
                searchElements.forEach { el ->
                    val link = if (el.tagName() == "a") el else el.selectFirst("a")
                    val itemUrl = link?.attr("href") ?: el.attr("href")
                    val text = link?.text()?.ifEmpty { el.text() } ?: el.text()
                    val img = el.selectFirst("img")
                    val image = img?.attr("src")?.ifEmpty { img.attr("data-src") } ?: ""
                    if (text.isNotBlank() && itemUrl.isNotBlank() && !itemUrl.startsWith("#") && !itemUrl.contains("javascript:") && !isIgnoredLink(text, itemUrl)) {
                        if (itemUrl.contains("/Device/") || itemUrl.contains("/Guide/") || itemUrl.contains("/Wiki/")) {
                            results.add(GuideCategory(text = text.trim(), image = image, url = itemUrl))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return results.distinctBy { it.url }
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