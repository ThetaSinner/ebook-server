package org.thetasinner.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "E-Book file not found")
public class EBookFileNotFoundException extends EBookDataServiceException {
  public EBookFileNotFoundException(String message) {
    super(message);
  }
}
