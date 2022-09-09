package com.example;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.multipart.MultipartBody;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;
import reactor.core.publisher.Flux;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class DemoTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    @Client("/v1")
    HttpClient client;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testUpload() {
        var builder = MultipartBody.builder();
        for(int i=0; i < 100; i++){
            builder.addPart("data", readFile("Upload.pdf"));
        }
        MultipartBody multipartBody = builder.build();

        var request = HttpRequest.POST("/upload", multipartBody)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON_TYPE);

        Flux<HttpResponse> responseFlux = Flux.from(client.exchange(request));

        HttpResponse response = responseFlux.blockFirst();
        assertEquals(200, response.getStatus().getCode());
    }

    public static File readFile(String path) {
        return Optional.ofNullable(DemoTest.class.getResource(path.startsWith(File.separator) ? path : File.separator + path))
                .map(url -> {
                    try {
                        return new File(url.toURI());
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(IllegalArgumentException::new);
    }
}
