package com.example;

import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.multipart.StreamingFileUpload;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Singleton
@Controller("/v1")
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);
    AtomicLong fileCounter = new AtomicLong(0);

    /*
    @Post(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA)
    @SingleResult
    public Publisher<HttpResponse> upload(Publisher<StreamingFileUpload> data) {
        return Flux.from(data)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap((StreamingFileUpload upload) -> {
                    LOG.info("File Name: {}", upload.getFilename());
                    return Flux.from(upload)
                            .map((pd) -> {
                                try {
                                    byte [] bytes = pd.getBytes();
                                    LOG.info("Part Data: {}", bytes.length);
                                    return bytes;
                                } catch (IOException e) {
                                    throw Exceptions.propagate(e);
                                }
                            });
                })
                .collect(LongAdder::new, (adder, bytes) -> adder.add((long)bytes.length))
                .map((adder) -> {
                    return HttpResponse.ok(adder.longValue());
                });
    }*/

    @Post(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA)
    @SingleResult
    public Publisher<HttpResponse> receiveMultiplePublishers(Publisher<Publisher<byte[]>> data) {
        return Flux.from(data)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap((Publisher<byte[]> upload) -> {
                    LOG.info("File Count: {}", fileCounter.incrementAndGet());
                    return Flux.from(upload).map((bytes) -> {
                        LOG.info("Byte size: {}", bytes.length);
                        return bytes;
                    });
                }).collect(LongAdder::new, (adder, bytes) -> adder.add((long) bytes.length))
                .map((adder) -> {
                    return HttpResponse.ok(adder.longValue());
                });
    }
}
