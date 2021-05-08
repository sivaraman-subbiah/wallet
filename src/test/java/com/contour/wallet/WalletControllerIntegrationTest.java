package com.contour.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

/**
 * @author SIVA
 *
 */

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class WalletControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String getBalanceUrl = "/api/v1/wallet/{walletId}/balance";
	private static final String addCoinsToWalletUrl = "/api/v1/wallet/{walletId}/coins";
	private static final String payWithWalletUrl = "/api/v1/wallet/{walletId}/{coin}";

	@Test
	public void addCoinsToWallet() {
	
		HttpEntity<String> request = new HttpEntity<String>("[1,1,2,2,3]");
		ResponseEntity<String> response = this.restTemplate.exchange("http://localhost:" + port + addCoinsToWalletUrl,HttpMethod.POST,
				request, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[1, 1, 2, 2, 3]", response.getBody());
	}
	
	@Test
	public void add0CoinToWallet() {
		
		HttpEntity<String> request = new HttpEntity<String>("[1,1,0,2,3]");
		ResponseEntity<String> response = this.restTemplate.exchange("http://localhost:" + port + addCoinsToWalletUrl,HttpMethod.POST,
				request, String.class,"1");
		assertEquals(400,response.getStatusCodeValue());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[1, 1, 2, 2, 3]", response.getBody());
	}
	
	@Test
	public void addNegativeCoinsWallet() {
		HttpEntity<String> request = new HttpEntity<String>("[1,1,-2,2,3]");
		ResponseEntity<String> response = this.restTemplate.exchange("http://localhost:" + port + addCoinsToWalletUrl,HttpMethod.POST,
				request, String.class,"1");
		assertEquals(400,response.getStatusCodeValue());
		
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[1, 1, 2, 2, 3]", response.getBody());
	}
	
	@Test
	public void addDecimalCoinsWallet() {
		HttpEntity<String> request = new HttpEntity<String>("[1,1,2.3,2,3]");
		ResponseEntity<String> response = this.restTemplate.exchange("http://localhost:" + port + addCoinsToWalletUrl,HttpMethod.POST,
				request, String.class,"1");
		assertEquals(400,response.getStatusCodeValue());
		
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[1, 1, 2, 2, 3]", response.getBody());
	}
	
	@Test
	public void walletBalanceForValidId() {
		ResponseEntity<String> response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[1, 1, 2, 2, 3]", response.getBody());
	}
	
	@Test
	public void walletBalanceForInvalidId() {
		ResponseEntity<String> response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"2");
		assertEquals(404,response.getStatusCodeValue());
	}
	
	@Test
	public void pay1WithoutChange() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","1");
		assertEquals(200,response.getStatusCodeValue());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[1, 2, 2, 3]", response.getBody());
	}
	
	@Test
	public void pay3WithoutChange() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","3");
		assertEquals(200,response.getStatusCodeValue());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[2, 3]", response.getBody());
	}

	@Test
	public void pay1WithChange() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","1");
		assertEquals(200,response.getStatusCodeValue());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[1, 3]", response.getBody());
	}
	
	@Test
	public void pay2WithChange() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","2");
		assertEquals(200,response.getStatusCodeValue());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[2]", response.getBody());
	}
	
	@Test
	public void pay0BadRequest() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","0");
		assertEquals(400,response.getStatusCodeValue());
		assertEquals("invalid number of coins".toLowerCase(), response.getBody().toLowerCase());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[2]", response.getBody());
	}
	
	@Test
	public void payNegativeBadRequest() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","-2");
		assertEquals(400,response.getStatusCodeValue());	
		assertEquals("invalid number of coins".toLowerCase(), response.getBody().toLowerCase());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[2]", response.getBody());
	}
	
	@Test
	public void payDecimalBadRequest() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","1.1");
		assertEquals(400,response.getStatusCodeValue());	
		assertEquals("invalid number of coins".toLowerCase(), response.getBody().toLowerCase());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertEquals("[2]", response.getBody());
	}
	
	@Test
	public void payWithoutEnoughBalance() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","5");
		assertEquals(200,response.getStatusCodeValue());
		
		response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"1");
		assertEquals(400,response.getStatusCodeValue());
		assertEquals("not enough balance".toLowerCase(), response.getBody().toLowerCase());
	}
	
	
	
	

}
