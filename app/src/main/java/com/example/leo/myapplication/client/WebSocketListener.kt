package com.example.leo.myapplication.client

import com.example.leo.myapplication.util.Logger
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

object WebSocketListener : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Logger.logError("WebSocket onOpen:$response")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Logger.logError("WebSocket onFailure:$response")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Logger.logError("WebSocket onClosing:$reason")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Logger.logError("WebSocket onMessage:$text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Logger.logError("WebSocket onMessage:$bytes")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Logger.logError("WebSocket onClosed:$reason")
    }
}
