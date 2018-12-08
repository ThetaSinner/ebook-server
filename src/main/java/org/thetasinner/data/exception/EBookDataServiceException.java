package org.thetasinner.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "E-Book data service encountered an error")
public class EBookDataServiceException extends RuntimeException {
  public EBookDataServiceException(String message) {
    super(message);
  }

  public EBookDataServiceException(String message, Exception cause) {
    super(message, cause);
  }
}
