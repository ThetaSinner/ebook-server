package org.thetasinner.web.error;

public class EBookControllerException extends RuntimeException {
  public EBookControllerException(String message, Exception cause) {
    super(message, cause);
  }
}
