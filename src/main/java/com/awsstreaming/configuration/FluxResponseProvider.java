package com.awsstreaming.configuration;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import com.awsstreaming.domain.FluxResponse;

import reactor.core.publisher.Flux;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class FluxResponseProvider implements AsyncResponseTransformer<GetObjectResponse, FluxResponse> {

	private FluxResponse response;

	@Override
	public CompletableFuture<FluxResponse> prepare() {
		response = new FluxResponse();
		return response.getFluxResponse();
	}

	@Override
	public void onResponse(GetObjectResponse sdkResponse) {
		this.response.setSdkResponse(sdkResponse);
	}

	@Override
	public void onStream(SdkPublisher<ByteBuffer> publisher) {
		response.setFlux(Flux.from(publisher));
		response.getFluxResponse().complete(response);
	}

	@Override
	public void exceptionOccurred(Throwable error) {
		response.getFluxResponse().completeExceptionally(error);
	}

}
