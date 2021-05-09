/**
 * 
 */
package com.contour.wallet.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.contour.wallet.exception.InsufficientBalance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SIVA
 *
 */
@Entity(name = "WALLET_COINS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "coins")
	private int[] coins;

	@Column(name = "LAST_UPDATE_TIME")
	private LocalDateTime lastUpdateTime;

	@Version
	private int version;

	public Wallet(Long id, int[] coins, LocalDateTime lastUpdateTime) {
		this.id = id;
		this.coins = coins;
		this.lastUpdateTime = lastUpdateTime;
	}

	public void debit(int coin) throws InsufficientBalance {
		synchronized (this) {
			Arrays.sort(this.getCoins());
			int[] availableCoins = this.getCoins();
			int balance = coin;
			for (int i = 0; i < availableCoins.length; i++) {
				balance = balance - availableCoins[i];
				if (balance <= 0) {
					availableCoins[i] = Math.abs(balance);
					break;
				}
				availableCoins[i] = 0;
			}
			if (balance > 0) {
				throw new InsufficientBalance();
			}

			int[] updatedCoins = Arrays.stream(availableCoins).filter(c -> c > 0).toArray();
			this.setCoins(updatedCoins);
		}
	}

}
