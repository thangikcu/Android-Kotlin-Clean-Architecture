package com.development.clean.util.debug

import java.io.EOFException
import java.io.IOException
import java.net.HttpURLConnection
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.TimeUnit
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.http.StatusLine.Companion.HTTP_CONTINUE
import okio.Buffer
import okio.GzipSource

class HttpLoggingInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val logInfo = Logcat.createLogInfo(request.url.toString())

        logInfo.addContent(Thread.currentThread().toString(), "Run on")
        logInfo.addLine()

        val requestBody = request.body

        val connection = chain.connection()

        logInfo.addContent(request.method, "Method")

        if (connection != null) {
            logInfo.addContent(connection.protocol().toString(), "Protocol")
        }

        val headers = request.headers

        if (requestBody != null) {
            // Request body headers are only present when installed as a network interceptor. When not
            // already present, force them to be included (if available) so their values are known.
            requestBody.contentType()?.let {
                if (headers["Content-Type"] == null) {
                    logInfo.addContent(it.toString(), "Content-Type")
                }
            }
            if (requestBody.contentLength() != -1L) {
                if (headers["Content-Length"] == null) {
                    logInfo.addContent(requestBody.contentLength().toString(), "Content-Length")
                }
            }
        }

        for (i in 0 until headers.size) {
            val value = headers.value(i)
            logInfo.addContent(value, headers.name(i))
        }

        val contentRequestBody: String

        when {
            requestBody == null -> {
                contentRequestBody = "<-- request body null"
            }
            bodyHasUnknownEncoding(request.headers) -> {
                contentRequestBody = "<-- encoded body omitted"
            }
            requestBody.isDuplex() -> {
                contentRequestBody = "<-- duplex request body omitted"
            }
            requestBody.isOneShot() -> {
                contentRequestBody = "<-- one-shot body omitted"
            }
            else -> {
                val buffer = Buffer()
                requestBody.writeTo(buffer)

                val contentType = requestBody.contentType()
                val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8

                contentRequestBody = if (buffer.isProbablyUtf8()) {
                    buffer.readString(charset)
                } else {
                    "(binary ${requestBody.contentLength()}-byte body omitted)"
                }
            }
        }

        logInfo.addContent(contentRequestBody, "Request body")

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logInfo.addContent("<-- HTTP FAILED: $e")
            logInfo.commitLog()
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"

        logInfo.addLine()

        logInfo.addContent(response.code.toString(), "Response code")

        if (response.message.isNotEmpty()) {
            logInfo.addContent(response.message, "Response message")
        }
        logInfo.addContent("(${tookMs}ms, $bodySize body})", "Time")

        val responseHeaders = response.headers
        for (i in 0 until responseHeaders.size) {
            val value = responseHeaders.value(i)
            logInfo.addContent(value, responseHeaders.name(i))
        }

        var contentResponseBody = ""

        if (!response.promisesBody()) {
            contentResponseBody = ""
        } else if (bodyHasUnknownEncoding(responseHeaders)) {
            contentResponseBody = "<-- encoded body omitted"
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer

            var gzippedLength: Long? = null
            if ("gzip".equals(responseHeaders["Content-Encoding"], ignoreCase = true)) {
                gzippedLength = buffer.size
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            val contentType = responseBody.contentType()
            val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8

            if (!buffer.isProbablyUtf8()) {
                contentResponseBody = "<-- binary ${buffer.size}-byte body omitted"
            } else {
                if (contentLength != 0L) {
                    contentResponseBody = buffer.clone().readString(charset)
                }

                contentResponseBody += if (gzippedLength != null) {
                    "\n(${buffer.size}-byte, $gzippedLength-gzipped-byte body)"
                } else {
                    "\n(${buffer.size}-byte body)"
                }
            }
        }

        logInfo.addContent(contentResponseBody, "Response body")
        logInfo.commitLog()
        return response
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
            !contentEncoding.equals("gzip", ignoreCase = true)
    }
}

internal fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}

/** Returns the Content-Length as reported by the response headers. */
fun Response.headersContentLength(): Long {
    return headers["Content-Length"]?.toLongOrDefault(-1L) ?: -1L
}

fun String.toLongOrDefault(defaultValue: Long): Long {
    return try {
        toLong()
    } catch (_: NumberFormatException) {
        defaultValue
    }
}

fun Response.promisesBody(): Boolean {
    // HEAD requests never yield a body regardless of the response headers.
    if (request.method == "HEAD") {
        return false
    }

    val responseCode = code
    if ((responseCode < HTTP_CONTINUE || responseCode >= 200) &&
        responseCode != HttpURLConnection.HTTP_NO_CONTENT &&
        responseCode != HttpURLConnection.HTTP_NOT_MODIFIED
    ) {
        return true
    }

    // If the Content-Length or Transfer-Encoding headers disagree with the response code, the
    // response is malformed. For best compatibility, we honor the headers.
    if (headersContentLength() != -1L ||
        "chunked".equals(header("Transfer-Encoding"), ignoreCase = true)
    ) {
        return true
    }

    return false
}
