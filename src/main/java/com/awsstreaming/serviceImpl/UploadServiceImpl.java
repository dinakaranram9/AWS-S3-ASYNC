package com.awsstreaming.serviceImpl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.awsstreaming.exception.UploadFailedException;
import com.awsstreaming.service.UploadService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

	@Autowired
	private S3AsyncClient s3client;

	@Value("${amazonProperties.bucketName}")
	private String defaultBucketName;

	@Override
	public CompletableFuture<PutObjectResponse> uploadFile(HttpHeaders headers, Flux<ByteBuffer> body, String fileKey) {
		long length = headers.getContentLength();
		if (length < 0) {
			throw new UploadFailedException(HttpStatus.BAD_REQUEST.value(),
					Optional.of("required header missing: Content-Length"));
		}

		Map<String, String> metadata = new HashMap<String, String>();
		MediaType mediaType = headers.getContentType();
		if (mediaType == null) {
			mediaType = MediaType.APPLICATION_OCTET_STREAM;
		}
		log.info("Uploading: file={}, mediaType{}, length={}", fileKey, mediaType, length);
		return s3client.putObject(
				PutObjectRequest.builder().bucket(defaultBucketName).contentLength(length).key(fileKey.toString())
						.contentType(mediaType.toString()).metadata(metadata).build(),
				AsyncRequestBody.fromPublisher(body));
	}

	public void validateResult(PutObjectResponse result) {
		if (result.sdkHttpResponse() == null || !result.sdkHttpResponse().isSuccessful()) {
			throw new UploadFailedException(result);
		}
	}
}
