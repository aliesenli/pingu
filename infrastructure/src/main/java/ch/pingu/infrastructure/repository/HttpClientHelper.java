package ch.pingu.infrastructure.repository;

import java.net.URI;
import java.net.http.HttpRequest;

class HttpClientHelper {

    private HttpClientHelper() {}

    static HttpRequest.Builder requestBuilder(String url, String token) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token);
    }
}
