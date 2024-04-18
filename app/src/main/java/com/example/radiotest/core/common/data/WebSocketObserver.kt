package com.example.radiotest.core.common.data

import android.util.Log
import com.example.radiotest.feature.player.data.RadioResponse
import com.example.radiotest.feature.player.presentation.contract.model.Song
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketObserver @Inject constructor(private val okHttpClient: OkHttpClient) :
    WebSocketListener() {

    private lateinit var listener: WebSocketListener

    private lateinit var socket: WebSocket

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun observeSongs(): Flow<Song> = callbackFlow {
        listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d("TESTER", "$text")
                val jsonData = json.parseToJsonElement(text).jsonObject
                val isJsonObjectForSongUpdates = jsonData.containsKey(KEY_CONNECT)
                        || (jsonData.containsKey(KEY_CHANNEl)
                        && jsonData[KEY_CHANNEl]?.jsonPrimitive?.content == CHANNEL_NAME)
                if (jsonData.isNotEmpty() && isJsonObjectForSongUpdates) {
                    try {
                        val songResponse = if (jsonData.containsKey("connect")) {
                            attemptInitialDataDecoding(jsonData)
                        } else {
                            json.decodeFromString<RadioResponse>(text)
                        }
                        val songData = songResponse?.pub?.data?.np?.nowPlaying?.song
                        Log.d("TESTER", "song: $songData")
                        trySendBlocking(
                            Song(
                                artist = songData?.artist,
                                title = songData?.title,
                                artUrl = songData?.art,
                            )
                        )
                    } catch (e: Throwable) {
                        Log.d("WebSocketObserver", "Parsing failed: $e")
                    }
                }
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d("TESTER", "onOpen:")
                webSocket.send(SOCKET_INIT_AZURA_MESSAGE)
            }
        }
        socket = okHttpClient.newWebSocket(
            request = Request.Builder().url(SOCKET_URL).build(),
            listener = listener,
        )
        awaitClose()
    }

    private fun attemptInitialDataDecoding(jsonObject: JsonObject): RadioResponse? {
        val responseData = jsonObject[KEY_CONNECT]
            ?.jsonObject?.get("data")
            ?.jsonArray
            ?.first {
                val dataObject = it.jsonObject
                dataObject.containsKey(KEY_CHANNEl) && dataObject[KEY_CHANNEl]!!.jsonPrimitive.content == CHANNEL_NAME
            }
        return responseData?.let { json.decodeFromJsonElement(it) }
    }

    fun close() {
        socket.close(NORMAL_SOCKET_CLOSE_CODE, "")
    }
}

private const val NORMAL_SOCKET_CLOSE_CODE = 1000

private const val SOCKET_INIT_AZURA_MESSAGE =
    "{ \"subs\": { \"station:hristijansko_radio\": {}, \"global:time\": {} }}"

private const val SOCKET_URL = "wss://a7.asurahosting.com/api/live/nowplaying/websocket"
private const val CHANNEL_NAME = "station:hristijansko_radio"
private const val KEY_CONNECT = "connect"
private const val KEY_CHANNEl = "channel"