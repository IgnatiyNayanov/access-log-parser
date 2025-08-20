public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, PATCH, CONNECT, UNKNOWN;

    public static HttpMethod fromString(String method) {
        if (method == null || method.isEmpty()) {
            return UNKNOWN;
        }
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
