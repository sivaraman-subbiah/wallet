/**
 * 
 */
package com.contour.wallet.exception;

/**
 * @author SIVA
 *
 */
public class InvalidCoinException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8855027677872637909L;

	public InvalidCoinException(String message,Throwable cause) {
		super(message,cause);
	}
	
	public InvalidCoinException(String message) {
		super(message);
	}
	
	public InvalidCoinException() {}
}
