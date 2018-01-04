package org.thetasinner.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Service {
    @RequestMapping("/hello")
    public @ResponseBody Data hello(@RequestParam(name="name", defaultValue = "ThetaSinner") String name) {
        return new Data(name);
    }
}
