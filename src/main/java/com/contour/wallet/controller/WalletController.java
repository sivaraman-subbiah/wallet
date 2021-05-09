package com.contour.wallet.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.contour.wallet.exception.InvalidCoinException;
import com.contour.wallet.exception.InsufficientBalance;
import com.contour.wallet.exception.WalletNotFoundException;
import com.contour.wallet.service.IWalletService;

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
	
	@Autowired
	private IWalletService walletService;
	
	/**
	 * @param walletId
	 * @param coins
	 * @return
	 */
	@Operation(summary = "Add coins to wallet")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Added coins successfully"),
			@ApiResponse(responseCode = "404", description = "Wallet Id not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "500", description = "Internal server error")})
	@PostMapping(value = "/{walletId}/coins",consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> addCoins(@PathVariable("walletId") final Long walletId
			,@RequestBody(required = true) final String[] coins ) {
		
		logger.info("Adding coins to wallet - id: {} , coins :{}", walletId, coins);
		
		try {
			walletService.addCoins(walletId, coins);
		} catch (WalletNotFoundException e) {
			logger.error("Wallet Not Found", e);
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Wallet not found.");
		} catch (InvalidCoinException e) {
			logger.error("Invalid coin", e);
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, e.getMessage());
		} catch(Exception e) {
			logger.error("Error while adding coins",e);
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, "Error while adding coins.");
		}
		
		return ResponseEntity.ok().build();
	}
	
	/**
	 * @param walletId
	 * @return
	 */
	@Operation(summary = "Get wallet coins")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the wallet balance"),
			@ApiResponse(responseCode = "404", description = "Wallet Id not found", content = @Content)})
	@GetMapping(value="/{walletId}/balance")
	public ResponseEntity<int[]> getCoins(@PathVariable("walletId") final Long walletId){
		logger.info("Fetching balance for wallet - id: {}", walletId);
		
		int[] coins = null;
		try {
			coins = walletService.getBalance(walletId);
		} catch (WalletNotFoundException e) {
			logger.error("Wallet Not Found", e);
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Wallet not found.");
		} catch(Exception e) {
			logger.error("Error while adding coins",e);
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, "Error while fetching balance.");
		}
		return ResponseEntity.ok(coins);
	}
	
	/**
	 * @param walletId
	 * @param coin
	 * @return
	 */
	@Operation(summary = "Pay coins from wallet")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Payment successful"),
			@ApiResponse(responseCode = "404", description = "Wallet Id not found", content = @Content),
			@ApiResponse(responseCode = "400", description = "Bad request"),
			@ApiResponse(responseCode = "500", description = "Internal server error")})
	@PutMapping("/{walletId}/pay/{coin}")
	public ResponseEntity<String> pay(@PathVariable("walletId") final Long walletId, @PathVariable("coin") final String coin) {
		
		logger.info("Paying coin from wallet - id: {} , coin :{}", walletId, coin);
		
		try {
			walletService.pay(walletId, coin);
		} catch (WalletNotFoundException e) {
			logger.error("Wallet Not Found", e);
			throw new ResponseStatusException( HttpStatus.NOT_FOUND, "Wallet not found.");
		} catch (InvalidCoinException e) {
			logger.error("Invalid coin", e);
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Invalid coin.");
		} catch (InsufficientBalance e) {
			logger.error("Insufficient balance in wallet", e);
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Insufficient balance in wallet.");
		}catch(Exception e) {
			logger.error("Error while adding coins",e);
			throw new ResponseStatusException( HttpStatus.INTERNAL_SERVER_ERROR, "Error while paying.");
		}
		
		return ResponseEntity.ok().build();
		
	}
}
