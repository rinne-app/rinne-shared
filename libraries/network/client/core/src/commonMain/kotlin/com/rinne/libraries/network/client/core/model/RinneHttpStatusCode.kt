package com.rinne.libraries.network.client.core.model

data class RinneHttpStatusCode(val value: Int, val description: String) {

    companion object {
        val Continue: RinneHttpStatusCode = RinneHttpStatusCode(100, "Continue")
        val SwitchingProtocols: RinneHttpStatusCode = RinneHttpStatusCode(101, "Switching Protocols")
        val Processing: RinneHttpStatusCode = RinneHttpStatusCode(102, "Processing")

        val OK: RinneHttpStatusCode = RinneHttpStatusCode(200, "OK")
        val Created: RinneHttpStatusCode = RinneHttpStatusCode(201, "Created")
        val Accepted: RinneHttpStatusCode = RinneHttpStatusCode(202, "Accepted")

        val NonAuthoritativeInformation: RinneHttpStatusCode =
            RinneHttpStatusCode(203, "Non-Authoritative Information")

        val NoContent: RinneHttpStatusCode = RinneHttpStatusCode(204, "No Content")
        val ResetContent: RinneHttpStatusCode = RinneHttpStatusCode(205, "Reset Content")
        val PartialContent: RinneHttpStatusCode = RinneHttpStatusCode(206, "Partial Content")
        val MultiStatus: RinneHttpStatusCode = RinneHttpStatusCode(207, "Multi-Status")

        val MultipleChoices: RinneHttpStatusCode = RinneHttpStatusCode(300, "Multiple Choices")
        val MovedPermanently: RinneHttpStatusCode = RinneHttpStatusCode(301, "Moved Permanently")
        val Found: RinneHttpStatusCode = RinneHttpStatusCode(302, "Found")
        val SeeOther: RinneHttpStatusCode = RinneHttpStatusCode(303, "See Other")
        val NotModified: RinneHttpStatusCode = RinneHttpStatusCode(304, "Not Modified")
        val UseProxy: RinneHttpStatusCode = RinneHttpStatusCode(305, "Use Proxy")
        val SwitchProxy: RinneHttpStatusCode = RinneHttpStatusCode(306, "Switch Proxy")
        val TemporaryRedirect: RinneHttpStatusCode = RinneHttpStatusCode(307, "Temporary Redirect")
        val PermanentRedirect: RinneHttpStatusCode = RinneHttpStatusCode(308, "Permanent Redirect")

        val BadRequest: RinneHttpStatusCode = RinneHttpStatusCode(400, "Bad Request")
        val Unauthorized: RinneHttpStatusCode = RinneHttpStatusCode(401, "Unauthorized")
        val PaymentRequired: RinneHttpStatusCode = RinneHttpStatusCode(402, "Payment Required")
        val Forbidden: RinneHttpStatusCode = RinneHttpStatusCode(403, "Forbidden")
        val NotFound: RinneHttpStatusCode = RinneHttpStatusCode(404, "Not Found")
        val MethodNotAllowed: RinneHttpStatusCode = RinneHttpStatusCode(405, "Method Not Allowed")
        val NotAcceptable: RinneHttpStatusCode = RinneHttpStatusCode(406, "Not Acceptable")

        val ProxyAuthenticationRequired: RinneHttpStatusCode =
            RinneHttpStatusCode(407, "Proxy Authentication Required")

        val RequestTimeout: RinneHttpStatusCode = RinneHttpStatusCode(408, "Request Timeout")
        val Conflict: RinneHttpStatusCode = RinneHttpStatusCode(409, "Conflict")
        val Gone: RinneHttpStatusCode = RinneHttpStatusCode(410, "Gone")
        val LengthRequired: RinneHttpStatusCode = RinneHttpStatusCode(411, "Length Required")
        val PreconditionFailed: RinneHttpStatusCode = RinneHttpStatusCode(412, "Precondition Failed")
        val PayloadTooLarge: RinneHttpStatusCode = RinneHttpStatusCode(413, "Payload Too Large")
        val RequestURITooLong: RinneHttpStatusCode = RinneHttpStatusCode(414, "Request-URI Too Long")

        val UnsupportedMediaType: RinneHttpStatusCode = RinneHttpStatusCode(415, "Unsupported Media Type")

        val RequestedRangeNotSatisfiable: RinneHttpStatusCode =
            RinneHttpStatusCode(416, "Requested Range Not Satisfiable")

        val ExpectationFailed: RinneHttpStatusCode = RinneHttpStatusCode(417, "Expectation Failed")
        val UnprocessableEntity: RinneHttpStatusCode = RinneHttpStatusCode(422, "Unprocessable Entity")
        val Locked: RinneHttpStatusCode = RinneHttpStatusCode(423, "Locked")
        val FailedDependency: RinneHttpStatusCode = RinneHttpStatusCode(424, "Failed Dependency")
        val TooEarly: RinneHttpStatusCode = RinneHttpStatusCode(425, "Too Early")
        val UpgradeRequired: RinneHttpStatusCode = RinneHttpStatusCode(426, "Upgrade Required")
        val TooManyRequests: RinneHttpStatusCode = RinneHttpStatusCode(429, "Too Many Requests")

        val RequestHeaderFieldTooLarge: RinneHttpStatusCode =
            RinneHttpStatusCode(431, "Request Header Fields Too Large")

        val InternalServerError: RinneHttpStatusCode = RinneHttpStatusCode(500, "Internal Server Error")
        val NotImplemented: RinneHttpStatusCode = RinneHttpStatusCode(501, "Not Implemented")
        val BadGateway: RinneHttpStatusCode = RinneHttpStatusCode(502, "Bad Gateway")
        val ServiceUnavailable: RinneHttpStatusCode = RinneHttpStatusCode(503, "Service Unavailable")
        val GatewayTimeout: RinneHttpStatusCode = RinneHttpStatusCode(504, "Gateway Timeout")

        val VersionNotSupported: RinneHttpStatusCode =
            RinneHttpStatusCode(505, "HTTP Version Not Supported")

        val VariantAlsoNegotiates: RinneHttpStatusCode = RinneHttpStatusCode(506, "Variant Also Negotiates")
        val InsufficientStorage: RinneHttpStatusCode = RinneHttpStatusCode(507, "Insufficient Storage")
    }
}