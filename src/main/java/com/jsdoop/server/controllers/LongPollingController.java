package com.jsdoop.server.controllers;

import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;


@RestController
public class LongPollingController {
	private static final Logger log = LoggerFactory.getLogger(LongPollingController.class);
	
	@GetMapping(value="/alive")
	public DeferredResult<ResponseEntity<?>> handleReqDefResult(@RequestParam(name = "username") String username, 
			@RequestParam(name = "info_worker") String infoWorker) {
		log.info("Received async-deferredresult request");
	    DeferredResult<ResponseEntity<?>> output = new DeferredResult<>();
	    
	    ForkJoinPool.commonPool().submit(() -> {
	        log.info("Processing in separate thread");
	        try {
	            Thread.sleep(6000);
	        } catch (InterruptedException e) {
	        }
	        output.setResult(ResponseEntity.ok("ok"));
	    });
	    
	    log.info("servlet thread freed");
	    return output;
	}	
	
}
