package org.thetasinner.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody DefaultError handleException(HttpServletRequest req, Exception e) {
        e.printStackTrace(); // TODO logging
        return new DefaultError(req.getRequestURI(), e.getLocalizedMessage());
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
