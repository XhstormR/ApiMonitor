package com.example.leo.monitor.client

import com.example.leo.monitor.model.parcel.DexPayload
import com.example.leo.monitor.model.request.LogUploadRequest
import com.example.leo.monitor.model.response.Response
import com.example.leo.monitor.model.response.TaskResponse
import com.topjohnwu.superuser.io.SuFileInputStream
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

    private val OCTET_STREAM_TYPE = MediaType.get("application/octet-stream")

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Backend.API_URL)
            .addConverterFactory(MoshiConverterFactory.create())
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

        val bytes = SuFileInputStream.open(payload).buffered().use { it.readBytes() }
        val formData = RequestBody.create(OCTET_STREAM_TYPE, bytes)
            .let { MultipartBody.Part.createFormData("file", payload.name, it) }

        backend.uploadDex(appHash, formData)
    }

    suspend fun uploadLog(logUploadRequest: LogUploadRequest) = doResponseAction {
        val (appHash, payload) = logUploadRequest

        val formData = RequestBody.create(OCTET_STREAM_TYPE, payload)
            .let { MultipartBody.Part.createFormData("file", payload.name, it) }

        backend.uploadLog(appHash, formData)
    }

    private suspend fun <R : Any> doResponseAction(action: suspend () -> Response<R>): R {
        val (code, message, result) = action()
        check(code == 200) { message }
        return checkNotNull(result)
    }
}
