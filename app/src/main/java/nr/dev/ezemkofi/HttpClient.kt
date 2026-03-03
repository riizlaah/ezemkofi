package nr.dev.ezemkofi

import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class HttpRequest(
    val url: String,
    val method: String = "GET",
    val body: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val timeout: Int = 10000
)

data class HttpResponse(
    val code: Int,
    val body: String? = null,
    val bytes: ByteArray? = null,
    val headers: Map<String, List<String>> = emptyMap(),
    val errors: String? = null
)

data class User(
    val id: Int,
    val fullname: String,
    val username: String,
    val email: String
)

data class Category(
    val id: Int,
    val name: String
)

data class Coffee(
    val id: Int,
    val categoryName: String,
    val name: String,
    val rating: Double,
    val price: Double,
    val imgPath: String
)

object HttpClient {
    const val ADDRESS = "http://10.0.2.2:5000/"
    var accessToken = ""

    var user by mutableStateOf<User?>(null)


    fun send(req: HttpRequest, getByte: Boolean = false): HttpResponse {
        val conn = URL(req.url).openConnection() as HttpURLConnection
        return try {
            conn.requestMethod = req.method
            conn.readTimeout = req.timeout
            conn.connectTimeout = req.timeout

            req.headers.forEach { (key, value) -> conn.setRequestProperty(key, value) }

            if (req.body != null && req.method in listOf("POST", "PUT", "PATCH")) {
                conn.doOutput = true
                conn.getOutputStream().buffered().use { it.write(req.body.toByteArray()) }
            }

            conn.connect()
            val code = conn.responseCode
            val body = if (!getByte) {
                if (code in 200..299) {
                    conn.getInputStream().bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() }
                }
            } else {
                null
            }
            val bytes = if (getByte) {
                if (code in 200..299) {
                    conn.getInputStream().buffered().use { it.readBytes() }
                } else {
                    conn.errorStream?.buffered()?.use { it.readBytes() }
                }
            } else {
                null
            }
            HttpResponse(
                code,
                body,
                bytes,
                headers = conn.headerFields
            )
        } catch (e: Exception) {
            e.printStackTrace()
            HttpResponse(
                code = -1,
                errors = e.message ?: "Network Error"
            )
        } finally {
            conn.disconnect()
        }
    }

    suspend fun fetchImg(url: String): ImageBitmap? {
        val res = withContext(Dispatchers.IO) {
            send(HttpRequest(url), true)
        }
        return if(res.code == 200 && res.bytes != null) {
            val bitmap = BitmapFactory.decodeByteArray(res.bytes, 0, res.bytes.size)
            bitmap.asImageBitmap()
        } else {
            null
        }
    }

    suspend fun jsonReq(url: String, body: String = "", method: String = "GET"): String {
        val headers = mapOf("content-type" to "application/json")
        val res = withContext(Dispatchers.IO) {
            send(
                HttpRequest(
                    url,
                    method,
                    body = body.ifEmpty { null },
                    headers = if(accessToken.isNotEmpty()) headers + mapOf("authorization" to "Bearer $accessToken") else headers
                )
            )
        }
        if (res.code in 200..299 && res.body != null) {
            return res.body
        }
        println(res)
        return ""
    }

    suspend fun login(username: String, password: String): Boolean {
        val body = """{"username": "$username", "password": "$password"}"""
        val str = jsonReq(ADDRESS + "api/auth", body, "POST")
        if (str.isEmpty()) return false
        accessToken = str
        getMyInfo()
        return true
    }

    suspend fun register(
        username: String,
        password: String,
        fullname: String,
        email: String
    ): Boolean {
        val body =
            """{"username": "$username", "fullname": "$fullname", "email": "$email", "password": "$password"}"""
        val jsonStr = jsonReq(ADDRESS + "api/register", body, "POST")
        return jsonStr.isNotEmpty()
    }

    suspend fun getMyInfo() {
        if(accessToken.isEmpty()) return
        val jsonStr = jsonReq(ADDRESS + "api/me")
        if(jsonStr.isEmpty()) return
        val obj = JSONObject(jsonStr)
        user = User(
            id = obj.getInt("id"),
            fullname = obj.getString("fullName"),
            username = obj.getString("username"),
            email = obj.getString("email"),
        )
    }

    suspend fun getCategories(): List<Category> {
        if(accessToken.isEmpty()) return emptyList()
        val jsonStr = jsonReq(ADDRESS + "api/coffee-category")
        if(jsonStr.isEmpty()) return emptyList()
        val arr = JSONArray(jsonStr)
        val categories = mutableListOf<Category>()
        for(i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            categories.add(Category(id = obj.getInt("id"), name = obj.getString("name")))
        }
        return categories
    }

    suspend fun getCoffees(search: String =  "", categoryId: Int = -1): List<Coffee> {
        if(accessToken.isEmpty()) return emptyList()
        var url = ADDRESS + "api/coffee"
        if(search.trim().isNotEmpty()) {
            var encodedStr = URLEncoder.encode(search, "UTF-8")
            url += "?search=$search"
            if(categoryId > 0) {
                url += "&coffeeCategoryID=$categoryId"
            }
        } else {
            if(categoryId > 0) {
                url += "?coffeeCategoryID=$categoryId"
            }
        }
        val jsonStr = jsonReq(url)
        if(jsonStr.isEmpty()) return emptyList()
        val arr = JSONArray(jsonStr)
        val coffees = mutableListOf<Coffee>()
        for(i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            coffees.add(Coffee(
                id = obj.getInt("id"),
                name = obj.getString("name"),
                categoryName = obj.getString("category"),
                rating = obj.getDouble("rating"),
                price = obj.getDouble("price"),
                imgPath = obj.getString("imagePath"),
            ))
        }
        return coffees
    }
}