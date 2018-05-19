package org.thetasinner.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="REST interaction failed")
public class EbookControllerException extends RuntimeException {
    public EbookControllerException(String message) {
        super(message);
    }

    public EbookControllerException(String message, Exception cause) {
        super(message, cause);
    }
}
