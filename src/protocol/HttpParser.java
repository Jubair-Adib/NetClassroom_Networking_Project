package protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {

    public static HttpRequest parse(InputStream in) throws IOException {

        byte[] headerBytes = readUntilHeaderEnd(in);
        String headerText = new String(headerBytes, "UTF-8");

        String[] lines = headerText.split("\r\n");
        if (lines.length == 0) {
            throw new IOException("Empty HTTP request");
        }

        // Request line
        String[] requestLineParts = lines[0].split("\\s+");
        if (requestLineParts.length < 2) {
            throw new IOException("Invalid request line");
        }

        String method = requestLineParts[0];
        String path = requestLineParts[1];

        // Headers
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) continue;

            int colon = line.indexOf(':');
            if (colon > 0) {
                String key = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                headers.put(key, value);
            }
        }

        byte[] rawBody = null;
        String bodyText = null;

        String contentLengthStr = headers.get("Content-Length");
        if (contentLengthStr != null) {
            int contentLength = Integer.parseInt(contentLengthStr);

            rawBody = new byte[contentLength];
            int totalRead = 0;

            // 🔥 THIS LOOP IS THE FLOW-CONTROL DEMO
            while (totalRead < contentLength) {
                int read = in.read(rawBody, totalRead, contentLength - totalRead);
                if (read == -1) {
                    throw new IOException("Unexpected end of stream while reading body");
                }
                totalRead += read;
            }

            // Convert to text only if it's likely text
            String contentType = headers.get("Content-Type");
            if (contentType != null && contentType.startsWith("text")) {
                bodyText = new String(rawBody, "UTF-8");
            }
        }

        return new HttpRequest(method, path, headers, bodyText, rawBody);
    }

    private static byte[] readUntilHeaderEnd(InputStream in) throws IOException {

        byte[] buffer = new byte[8192];
        int pos = 0;

        int a = -1, b = -1, c = -1;

        while (true) {
            int d = in.read();
            if (d == -1) {
                throw new IOException("Stream closed while reading headers");
            }

            if (pos >= buffer.length) {
                byte[] newBuf = new byte[buffer.length * 2];
                System.arraycopy(buffer, 0, newBuf, 0, buffer.length);
                buffer = newBuf;
            }

            buffer[pos++] = (byte) d;

            if (a == '\r' && b == '\n' && c == '\r' && d == '\n') {
                break;
            }

            a = b;
            b = c;
            c = d;
        }

        byte[] result = new byte[pos];
        System.arraycopy(buffer, 0, result, 0, pos);
        return result;
    }
}
