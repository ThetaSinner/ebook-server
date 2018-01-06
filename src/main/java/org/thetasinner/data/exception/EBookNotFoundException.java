package org.thetasinner.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="E-Book not found")
public class EBookNotFoundException extends EBookDataServiceException {
    public EBookNotFoundException(String message) {
        super(message);
    }
}
