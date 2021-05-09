/**
 * 
 */
package com.contour.wallet.service;

import com.contour.wallet.exception.InsufficientBalance;
import com.contour.wallet.exception.InvalidCoinException;
import com.contour.wallet.exception.StaleDataException;
import com.contour.wallet.exception.WalletNotFoundException;

/**
 * @author SIVA
 *
 */
public interface IWalletService {

	public void addCoins(final Long walletId, final String[] coins) throws WalletNotFoundException, InvalidCoinException;
	public int[] getBalance(final Long walletId) throws WalletNotFoundException;
	public void pay(final Long walletId, String coin) throws WalletNotFoundException, InvalidCoinException, InsufficientBalance, StaleDataException;
}
