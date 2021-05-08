package com.contour.wallet.controller;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


/**
 * @author SIVA
 *
 */

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

	private static final Logger logger = LoggerFactory.getLogger(WalletController.class);
	
	/**
	 * @param walletId
	 * @return
	 */
	@Operation(summary = "Get wallet balance by its id")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the wallet balance"),
			@ApiResponse(responseCode = "404", description = "Wallet Id not found", content = @Content)})
	@GetMapping("/{walletId}/balance")
	public ResponseEntity<List<Integer>> getBalance(@PathVariable("walletId") final String walletId){
		logger.info("Fetch Balance for wallet id: {}", walletId);
		return ResponseEntity.ok(Arrays.asList(1,2,3));
	}
}
