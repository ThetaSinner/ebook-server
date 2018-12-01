package org.thetasinner.ebookserver.helper;

import org.springframework.stereotype.Service;

@Service
public class UrlHelper {
    public String buildRequestUrl(String uri, int port) {
        return String.format("http://localhost:%d%s", port, uri);
    }
}
