package com.awsstreaming.controller;

import java.nio.ByteBuffer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.awsstreaming.service.DownloadService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class DownloadFileController {

	@Autowired
	DownloadService downloadService;

	@GetMapping(path = "/{filekey}")
	Mono<ResponseEntity<Flux<ByteBuffer>>> downloadFile(@PathVariable("filekey") String filekey) {
		log.info("file download started for " + filekey);
		return Mono.fromFuture(downloadService.downloadFile(filekey)).map(response -> {
			downloadService.validateResult(response.getSdkResponse());
			String filename = downloadService.getMetadataItem(response.getSdkResponse(), "filename", filekey);
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, response.getSdkResponse().contentType())
					.header(HttpHeaders.CONTENT_LENGTH, Long.toString(response.getSdkResponse().contentLength()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
					.body(response.getFlux());
		});
	}

}
