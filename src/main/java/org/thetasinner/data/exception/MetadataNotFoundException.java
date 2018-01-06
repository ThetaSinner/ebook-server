package org.thetasinner.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="E-Book not found")
public class MetadataNotFoundException extends EBookDataServiceException {
    public MetadataNotFoundException(String message) {
        super(message);
    }
}
