package at.fh.swengb.hackensoellner

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class AuthResponse(val token: String) {
}