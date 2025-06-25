package it.mindtek.ruah.ws.interfaces

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class DownloadProgressResponseBody(
    private val responseBody: ResponseBody,
    private val progressListener: DownloadProgressListener?
) : ResponseBody() {
    override fun contentType(): MediaType? = responseBody.contentType()
    override fun contentLength(): Long = responseBody.contentLength()
    override fun source(): BufferedSource = source(responseBody.source()).buffer()

    private fun source(source: Source): Source = object : ForwardingSource(source) {
        var totalBytesRead: Long = 0L

        @Throws(IOException::class)
        override fun read(sink: Buffer, byteCount: Long): Long {
            val bytesRead: Long = super.read(sink, byteCount)
            val totalMB = responseBody.contentLength().convertToMB()
            // read() returns the number of bytes read, or -1 if this source is exhausted.
            if (bytesRead == -1L) {
                progressListener?.update(totalBytesRead.convertToMB(), totalMB, 100)
                return bytesRead
            }
            totalBytesRead += bytesRead
            val progress: Int = (totalBytesRead * 100 / responseBody.contentLength()).toInt()
            progressListener?.update(totalBytesRead.convertToMB(), totalMB, progress)
            return bytesRead
        }
    }
}

class DownloadProgressInterceptor(private val listener: DownloadProgressListener) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse: Response = chain.proceed(chain.request())
        return originalResponse.newBuilder().run {
            originalResponse.body?.let {
                body(DownloadProgressResponseBody(it, listener))
            }
            build()
        }
    }
}

interface DownloadProgressListener {
    fun update(readMB: Long, totalMB: Long, percent: Int)
}

fun Long.convertToMB(): Long = this / 1024 / 1024