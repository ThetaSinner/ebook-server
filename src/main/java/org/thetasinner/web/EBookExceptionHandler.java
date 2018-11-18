package org.thetasinner.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thetasinner.data.exception.EBookDataServiceInputValidationException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
class EBookExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(EBookExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public @ResponseBody DefaultError handleException(HttpServletRequest req, Exception e) {
        LOG.error("Converting exception to error response.", e);
        return new DefaultError(req.getRequestURI(), e.getLocalizedMessage());
    }

    @ExceptionHandler(EBookDataServiceInputValidationException.class)
    public @ResponseBody ResponseEntity<String> handleValidation(EBookDataServiceInputValidationException e) {
        LOG.error("Converting exception to error response.", e);
        var body = String.format("E-Book data service input failed validation [%s]", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private static class DefaultError {
        private String url;
        private String errorMessage;

        DefaultError(String url, String errorMessage) {
            this.url = url;
            this.errorMessage = errorMessage;
        }

        public String getUrl() {
            return url;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
