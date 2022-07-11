package com.awsstreaming.domain;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import lombok.Data;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Data
public class FluxResponse {

	final CompletableFuture<FluxResponse> fluxResponse = new CompletableFuture<>();
	private GetObjectResponse sdkResponse;
	private Flux<ByteBuffer> flux;
}