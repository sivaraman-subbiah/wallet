/**
 * 
 */
package com.contour.wallet.exception;

/**
 * @author SIVA
 *
 */
public class StaleDataException extends Exception{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5944437206105009671L;

	public StaleDataException(String message,Throwable cause) {
		super(message,cause);
	}
	
	public StaleDataException(String message) {
		super(message);
	}
	
	public StaleDataException() {}
}
