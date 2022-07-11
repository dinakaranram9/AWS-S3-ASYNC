package com.awsstreaming.configuration;

import java.net.URI;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.utils.StringUtils;

@Configuration
public class S3ClientConfiguration {

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	@Value("${amazonProperties.bucketName}")
	private String defaultBucketName;
	@Value("${amazonProperties.accessKey}")
	private String accessKey;
	@Value("${amazonProperties.secretKey}")
	private String secretKey;
	@Value("${amazonProperties.regionName}")
	private String regionName;

	@Bean
	public S3AsyncClient s3client(AwsCredentialsProvider credentialsProvider) {
		SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder().writeTimeout(Duration.ZERO).maxConcurrency(64)
				.build();
		S3Configuration serviceConfiguration = S3Configuration.builder().checksumValidationEnabled(false)
				.chunkedEncodingEnabled(true).build();
		S3AsyncClientBuilder asyncClientBuilder = S3AsyncClient.builder().httpClient(httpClient)
				.region(Region.of(regionName)).credentialsProvider(credentialsProvider)
				.serviceConfiguration(serviceConfiguration);

		if (endpointUrl != null) {
			asyncClientBuilder = asyncClientBuilder.endpointOverride(URI.create(endpointUrl));
		}
		return asyncClientBuilder.build();
	}

	@Bean
	public AwsCredentialsProvider awsCredentialsProvider() {
		if (StringUtils.isBlank(accessKey)) {
			return DefaultCredentialsProvider.create();
		} else {
			return () -> {
				return AwsBasicCredentials.create(accessKey, secretKey);
			};
		}
	}
}
