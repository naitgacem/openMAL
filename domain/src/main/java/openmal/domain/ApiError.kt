package openmal.domain

import androidx.annotation.Keep


@Keep
enum class ApiError {
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    NOT_FOUND,
    UNKNOWN,
}