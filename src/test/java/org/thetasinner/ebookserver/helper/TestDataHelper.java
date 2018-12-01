package org.thetasinner.ebookserver.helper;

import org.springframework.stereotype.Service;

@Service
public class TestDataHelper {
    public String getCurrentMethodName() {
        // Indexing to 2 removes 'getStackTrace' and the current method name 'getCurrentMethodName'
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}
