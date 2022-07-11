package com.awsstreaming.serviceImpl;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.awsstreaming.configuration.FluxResponseProvider;
import com.awsstreaming.domain.FluxResponse;
import com.awsstreaming.exception.DownloadFailedException;
import com.awsstreaming.service.DownloadService;

import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class DownloadServiceImpl implements DownloadService {

	@Autowired
	private S3AsyncClient s3client;

	@Value("${amazonProperties.bucketName}")
	private String defaultBucketName;

	@Override
	public CompletableFuture<FluxResponse> downloadFile(String fileKey) {
		GetObjectRequest request = GetObjectRequest.builder().bucket(defaultBucketName).key(fileKey).build();
		return s3client.getObject(request, new FluxResponseProvider());
	}

	public String getMetadataItem(GetObjectResponse sdkResponse, String key, String defaultValue) {
		for (Entry<String, String> entry : sdkResponse.metadata().entrySet()) {
			if (entry.getKey().equalsIgnoreCase(key)) {
				return entry.getValue();
			}
		}
		return defaultValue;
	}

	public void validateResult(GetObjectResponse response) {
		SdkHttpResponse sdkResponse = response.sdkHttpResponse();
		if (sdkResponse != null && sdkResponse.isSuccessful()) {
			return;
		}

		throw new DownloadFailedException(response);
	}

}
