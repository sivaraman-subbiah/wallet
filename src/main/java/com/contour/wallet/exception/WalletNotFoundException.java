/**
 * 
 */
package com.contour.wallet.exception;

/**
 * @author SIVA
 *
 */
public class WalletNotFoundException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1431386053998753154L;

	public WalletNotFoundException(String message,Throwable cause) {
		super(message,cause);
	}
	
	public WalletNotFoundException(String message) {
		super(message);
	}
	
	public WalletNotFoundException() {}
}
