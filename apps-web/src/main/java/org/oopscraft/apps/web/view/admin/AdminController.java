package org.oopscraft.apps.web.view.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * index
     * @return
     * @throws Exception
     */
    @GetMapping
    public ModelAndView index() throws Exception {
        ModelAndView modelAndView = new ModelAndView("admin/_admin.html");
        return modelAndView;
    }

}
