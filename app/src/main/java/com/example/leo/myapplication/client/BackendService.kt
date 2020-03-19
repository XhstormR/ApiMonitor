package com.example.leo.myapplication.client

import com.example.leo.myapplication.model.parcel.DexPayload
import com.example.leo.myapplication.model.response.Response
import com.example.leo.myapplication.model.response.TaskResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.WebSocket
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

object BackendService {

    interface Backend {

        companion object {
            const val API_URL = "http://192.168.2.128:1818"

            const val WS_URL = "ws://192.168.2.128:1818/socket/server/123"
        }

        @GET("/anon/apk/task")
        suspend fun getTask(): Response<TaskResponse>

        @POST("/anon/apk/finished/{sha256}")
        suspend fun finishTask(
            @Path("sha256") sha256: String
        ): Response<Int>

        @GET("/anon/apk/virusInfo/{sha256}")
        suspend fun getAppInfo(
            @Path("sha256") sha256: String
        ): Response<TaskResponse>

        @Streaming
        @GET("/anon/apk/download/{sha256}")
        suspend fun downloadApk(
            @Path("sha256") sha256: String
        ): ResponseBody

        @Multipart
        @POST("/anon/apk/upload")
        suspend fun uploadApk(
            @Part file: MultipartBody.Part
        ): Response<Int>

        @Multipart
        @POST("/anon/dex/{sha256}")
        suspend fun uploadDex(
            @Path("sha256") sha256: String,
            @Part file: MultipartBody.Part
        ): Response<Int>
    }

    private val backend: Backend

    private val websocket: WebSocket

    private val octet_stream = MediaType.get("application/octet-stream")

    init {
        val httpClient = OkHttpClient.Builder()
            .build()
        val retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(Backend.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val request = Request.Builder()
            .url(Backend.WS_URL)
            .build()

        backend = retrofit.create()

        websocket = httpClient.newWebSocket(request, WebSocketListener)
    }

    fun send(text: String) = websocket.send(text)

    suspend fun downloadApk(sha256: String) =
        backend.downloadApk(sha256)

    suspend fun getTask() = doResponseAction {
        backend.getTask()
    }

    suspend fun finishTask(sha256: String) = doResponseAction {
        backend.finishTask(sha256)
    }

    suspend fun uploadDex(dexPayload: DexPayload) = doResponseAction {
        val (apkHash, payload) = dexPayload

        val formData = RequestBody.create(octet_stream, payload)
            .let { MultipartBody.Part.createFormData("file", "${payload.size}.dex", it) }

        backend.uploadDex(apkHash, formData)
    }

    private suspend fun <R> doResponseAction(action: suspend () -> Response<R>): R {
        val (code, message, result) = action()
        if (code != 200) throw IllegalStateException(message)
        return result
    }
}
