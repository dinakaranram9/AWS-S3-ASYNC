package com.awsstreaming.controller;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.awsstreaming.domain.UploadResult;
import com.awsstreaming.service.UploadService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Dinakaran Ramadurai
 *
 */
@RestController
public class UploadFileController {

	@Autowired
	UploadService uploadService;

	@PostMapping("/upload")
	public Mono<ResponseEntity<UploadResult>> uploadFile(@RequestHeader HttpHeaders headers,
			@RequestBody Flux<ByteBuffer> body, @RequestParam String filename) {
		String fileKey = UUID.randomUUID().toString() + filename;
		return Mono.fromFuture(uploadService.uploadFile(headers, body, fileKey)).map((response) -> {
			uploadService.validateResult(response);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new UploadResult(HttpStatus.CREATED, new String[] { fileKey }));
		});
	}

}