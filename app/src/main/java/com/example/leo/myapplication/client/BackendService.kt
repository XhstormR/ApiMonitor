package com.example.leo.myapplication.client

import com.example.leo.myapplication.model.parcel.DexPayload
import com.example.leo.myapplication.model.request.LogUploadRequest
import com.example.leo.myapplication.model.response.Response
import com.example.leo.myapplication.model.response.TaskResponse
import com.topjohnwu.superuser.io.SuFileInputStream
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
        }

        @GET("/anon/apk/task")
        suspend fun getTask(): Response<TaskResponse>

        @POST("/anon/apk/finished/{sha256}")
        suspend fun finishTask(
            @Path("sha256") appHash: String
        ): Response<Int>

        @GET("/anon/apk/virusInfo/{sha256}")
        suspend fun getAppInfo(
            @Path("sha256") appHash: String
        ): Response<TaskResponse>

        @Streaming
        @GET("/anon/apk/download/{sha256}")
        suspend fun downloadApk(
            @Path("sha256") appHash: String
        ): ResponseBody

        @Multipart
        @POST("/anon/apk/upload")
        suspend fun uploadApk(
            @Part file: MultipartBody.Part
        ): Response<Int>

        @Multipart
        @POST("/anon/dex/{sha256}")
        suspend fun uploadDex(
            @Path("sha256") appHash: String,
            @Part file: MultipartBody.Part
        ): Response<Int>

        @Multipart
        @POST("/anon/log/{sha256}")
        suspend fun uploadLog(
            @Path("sha256") appHash: String,
            @Part file: MultipartBody.Part
        ): Response<Int>
    }

    private val backend: Backend

    private val octet_stream = MediaType.get("application/octet-stream")

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Backend.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        backend = retrofit.create()
    }

    suspend fun downloadApk(appHash: String) =
        backend.downloadApk(appHash)

    suspend fun getTask() = doResponseAction {
        backend.getTask()
    }

    suspend fun finishTask(appHash: String) = doResponseAction {
        backend.finishTask(appHash)
    }

    suspend fun uploadDex(dexPayload: DexPayload) = doResponseAction {
        val (appHash, payload) = dexPayload

        val bytes = SuFileInputStream(payload).buffered().use { it.readBytes() }
        val formData = RequestBody.create(octet_stream, bytes)
            .let { MultipartBody.Part.createFormData("file", "${bytes.size}.dex", it) }

        backend.uploadDex(appHash, formData)
    }

    suspend fun uploadLog(logUploadRequest: LogUploadRequest) = doResponseAction {
        val (appHash, payload) = logUploadRequest

        val formData = RequestBody.create(octet_stream, payload)
            .let { MultipartBody.Part.createFormData("file", "$appHash.log", it) }

        backend.uploadLog(appHash, formData)
    }

    private suspend fun <R> doResponseAction(action: suspend () -> Response<R>): R {
        val (code, message, result) = action()
        check(code == 200) { message }
        return result
    }
}
