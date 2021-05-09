package com.contour.wallet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

/**
 * @author SIVA
 *
 */

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@TestMethodOrder(OrderAnnotation.class)
public class WalletControllerIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String getBalanceUrl = "/api/v1/wallet/{walletId}/balance";
	private static final String addCoinsToWalletUrl = "/api/v1/wallet/{walletId}/coins";
	private static final String payWithWalletUrl = "/api/v1/wallet/{walletId}/pay/{coin}";
	private static HttpHeaders headers = new HttpHeaders();
	
	@BeforeAll
	public static void setup() {
		headers.setContentType(MediaType.APPLICATION_JSON);
		List<MediaType> accepts = new ArrayList<>(); accepts.add(MediaType.APPLICATION_JSON);
		//headers.setAccept(accepts);
	}
	
	@Test
	@Order(1)
	public void addCoinsToWallet() {
	
		HttpEntity<String> request = new HttpEntity<String>("[1,1,2,2,3]",headers);
		
		ResponseEntity<String> response = this.restTemplate.exchange("http://localhost:" + port + addCoinsToWalletUrl,HttpMethod.POST,
				request, String.class,"1");
		assertEquals(200,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{1, 1, 2, 2, 3}, getResponse.getBody());
	}
	
	@Test
	@Order(2)
	public void add0CoinToWallet() {
		HttpEntity<String> request = new HttpEntity<String>("[1,1,0,2,3]",headers);
		ResponseEntity<String> response = this.restTemplate.exchange("http://localhost:" + port + addCoinsToWalletUrl,HttpMethod.POST,
				request, String.class,"1");
		assertEquals(400,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{1,1,2,2,3}, getResponse.getBody());
	}
	
	@Test
	@Order(3)
	public void addNegativeCoinsWallet() {
		HttpEntity<String> request = new HttpEntity<String>("[1,1,-2,2,3]",headers);
		ResponseEntity<String> response = this.restTemplate.exchange("http://localhost:" + port + addCoinsToWalletUrl,HttpMethod.POST,
				request, String.class,"1");
		assertEquals(400,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{1, 1, 2, 2, 3}, getResponse.getBody());
	}
	
	@Test
	@Order(4)
	public void addDecimalCoinsWallet() {
		
		HttpEntity<String> request = new HttpEntity<String>("[1,1,2.3,2,3]",headers);
		
		ResponseEntity<String> response = this.restTemplate.exchange("http://localhost:" + port + addCoinsToWalletUrl,HttpMethod.POST,
				request, String.class,"1");
		assertEquals(400,response.getStatusCodeValue());
		
		
		ResponseEntity<int[]> getResponse  = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{1, 1, 2, 2, 3}, getResponse.getBody());
	}
	
	@Test
	@Order(5)
	public void walletBalanceForValidId() {
		ResponseEntity<int[]> response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,response.getStatusCodeValue());
		assertArrayEquals( new int []{1, 1, 2, 2, 3}, response.getBody());
	}
	
	@Test
	@Order(6)
	public void walletBalanceForInvalidId() { 
		ResponseEntity<String> response = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, String.class,"2");
		assertEquals(404,response.getStatusCodeValue());
	}
	
	@Test
	@Order(7)
	public void pay1WithoutChange() {
		ResponseEntity<int[]> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, int[].class,"1","1");
		assertEquals(200,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{1, 2, 2, 3}, getResponse.getBody());
	}
	
	@Test
	@Order(8)
	public void pay3WithoutChange() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","3");
		assertEquals(200,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{2, 3},getResponse.getBody());
	}

	@Test
	@Order(9)
	public void pay1WithChange() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","1");
		assertEquals(200,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{1, 3}, getResponse.getBody());
	}
	
	@Test
	@Order(10)
	public void pay2WithChange() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","2");
		assertEquals(200,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{2}, getResponse.getBody());
	}
	
	@Test
	@Order(11)
	public void pay0BadRequest() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","0");
		assertEquals(400,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{2}, getResponse.getBody());
	}
	
	@Test
	@Order(12)
	public void payNegativeBadRequest() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","-2");
		assertEquals(400,response.getStatusCodeValue());	
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{2}, getResponse.getBody());
	}
	
	@Test
	@Order(13)
	public void payDecimalBadRequest() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","1.1");
		assertEquals(400,response.getStatusCodeValue());	
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{2}, getResponse.getBody());
	}
	
	@Test
	@Order(14)
	public void payWithoutEnoughBalance() {
		ResponseEntity<String> response = this.restTemplate
				.exchange("http://localhost:" + port + payWithWalletUrl,HttpMethod.PUT,null, String.class,"1","5");
		assertEquals(400,response.getStatusCodeValue());
		
		ResponseEntity<int[]> getResponse = this.restTemplate
				.getForEntity("http://localhost:" + port + getBalanceUrl, int[].class,"1");
		assertEquals(200,getResponse.getStatusCodeValue());
		assertArrayEquals( new int []{2}, getResponse.getBody());
	}

}
