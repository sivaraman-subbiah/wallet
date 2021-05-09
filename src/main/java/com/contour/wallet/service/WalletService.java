/**
 * 
 */
package com.contour.wallet.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contour.wallet.exception.InsufficientBalance;
import com.contour.wallet.exception.InvalidCoinException;
import com.contour.wallet.exception.StaleDataException;
import com.contour.wallet.exception.WalletNotFoundException;
import com.contour.wallet.model.Wallet;
import com.contour.wallet.repository.WalletRepository;

/**
 * @author SIVA
 *
 */
@Service
public class WalletService implements IWalletService {

	private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

	@Autowired
	private WalletRepository walletRepository;

	@Override
	public void addCoins(Long walletId, String[] coins) throws WalletNotFoundException, InvalidCoinException {

		Wallet wallet = walletRepository.save(new Wallet(walletId, validateCoins(coins),LocalDateTime.now()));
		logger.debug("wallet with coins added : {}", wallet);

	}

	@Override
	public int[] getBalance(Long walletId) throws WalletNotFoundException {
		Wallet wallet = walletRepository.findById(walletId).orElseThrow(WalletNotFoundException::new);
		Arrays.sort(wallet.getCoins());
		return wallet.getCoins();

	}

	@Override
	public void pay(Long walletId, String coin) throws WalletNotFoundException, InvalidCoinException, InsufficientBalance,StaleDataException {
		
		int[] coins = validateCoins(new String[] {coin});
		Wallet wallet = walletRepository.findById(walletId).orElseThrow(WalletNotFoundException::new);
		
		wallet.debit(coins[0]);
		
		Wallet latestWallet = walletRepository.findById(walletId).orElseThrow(WalletNotFoundException::new);
		if(latestWallet.getLastUpdateTime().isEqual(wallet.getLastUpdateTime())) {
			wallet.setLastUpdateTime(LocalDateTime.now());
			walletRepository.saveAndFlush(wallet);
		}else {
			throw new StaleDataException();
		}
	}

	private int[] validateCoins(String[] coins) throws InvalidCoinException {
		boolean invalidCoin = Arrays.stream(coins).anyMatch(c -> !c.matches("[0-9]+"));

		if (invalidCoin) {
			throw new InvalidCoinException("Coin(s) should be positive non zero number");
		}

		int[] coinArr = Stream.of(coins).mapToInt(Integer::parseInt).filter(c -> c > 0).toArray();

		if (coinArr.length != coins.length) {
			throw new InvalidCoinException("Coin(s) cannot be zero or negative");
		}
		
		return coinArr;
	}
	

}
