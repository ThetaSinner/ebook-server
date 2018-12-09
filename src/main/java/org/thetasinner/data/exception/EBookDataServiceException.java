package org.thetasinner.data.exception;

public class EBookDataServiceException extends RuntimeException {
  public EBookDataServiceException(String message) {
    super(message);
  }

  public EBookDataServiceException(String message, Exception cause) {
    super(message, cause);
  }
}
