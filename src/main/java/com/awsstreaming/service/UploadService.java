package com.awsstreaming.service;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

public interface UploadService {

	CompletableFuture<PutObjectResponse> uploadFile(HttpHeaders headers, Flux<ByteBuffer> body, String fileKey);

	void validateResult(PutObjectResponse response);

}
