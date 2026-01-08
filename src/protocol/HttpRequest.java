package protocol;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> headers;

    // Text body (for chat)
    private final String body;

    // Raw body (for file upload)
    private final byte[] rawBody;

    public HttpRequest(String method,
                       String path,
                       Map<String, String> headers,
                       String body,
                       byte[] rawBody) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.rawBody = rawBody;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public byte[] getRawBody() {
        return rawBody;
    }
}
