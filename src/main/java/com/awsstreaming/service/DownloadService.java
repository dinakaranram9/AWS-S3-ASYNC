package com.awsstreaming.service;

import java.util.concurrent.CompletableFuture;

import com.awsstreaming.domain.FluxResponse;

import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public interface DownloadService {
	CompletableFuture<FluxResponse> downloadFile(String fileKey);

	void validateResult(GetObjectResponse sdkResponse);

	String getMetadataItem(GetObjectResponse sdkResponse, String string, String filekey);
}
