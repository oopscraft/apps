package org.oopscraft.apps.web.view;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {

    /**
     * login
     * @return
     * @throws Exception
     */
    @GetMapping
    @PreAuthorize("permitAll()")
    public ModelAndView index() throws Exception {
        ModelAndView modelAndView = new ModelAndView("/login/login.html");
        return modelAndView;
    }

}
