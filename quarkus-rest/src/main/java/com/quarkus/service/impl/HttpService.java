package com.quarkus.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;


@Singleton
public class HttpService {

    private final HttpClient httpClient = HttpClient.
            newBuilder().version(HttpClient.Version.HTTP_2).build();

    public Map<String, Object> fetchPosts(String url) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder().GET().
                uri(URI.create(url)).header("Accept",
                "application/json").build();
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.
                        BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> object = objectMapper.readValue(response.body(), Map.class);

        return  object;
    }

}