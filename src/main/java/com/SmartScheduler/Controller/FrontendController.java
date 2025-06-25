package com.SmartScheduler.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {

    @RequestMapping(value = { "/", "/{path:^(?!api$|index\\.html$|assets|index\\.css$).*$}/**" })
    public String forward() {
        return "forward:/index.html";
    }

}
