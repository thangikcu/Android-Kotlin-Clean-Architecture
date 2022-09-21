package com.development.clean.common.exception

class NetworkException constructor(
    override val message: String? =
        "Has no internet connection!",
) : Throwable(message)
