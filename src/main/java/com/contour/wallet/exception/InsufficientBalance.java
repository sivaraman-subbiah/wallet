/**
 * 
 */
package com.contour.wallet.exception;

/**
 * @author SIVA
 *
 */
public class InsufficientBalance extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5944437206105009671L;

	public InsufficientBalance(String message,Throwable cause) {
		super(message,cause);
	}
	
	public InsufficientBalance(String message) {
		super(message);
	}
	
	public InsufficientBalance() {}
}
