/**
 * 
 */
package com.contour.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.contour.wallet.model.Wallet;

/**
 * @author SIVA
 *
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

}
