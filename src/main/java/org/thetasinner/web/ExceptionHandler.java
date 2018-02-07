package org.thetasinner.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
class ExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody DefaultError handleException(HttpServletRequest req, Exception e) {
        LOG.error("Converting exception to error response.", e);
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
