package org.oopscraft.apps.web.view.admin;

import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.user.User;
import org.oopscraft.apps.core.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("admin/user")
@RequiredArgsConstructor
public class UserController {

   private final UserService userService;

    @GetMapping()
    public ModelAndView index() throws Exception {
        ModelAndView modelAndView = new ModelAndView("admin/user.html");
        modelAndView.addObject("userTypes", User.Type.values());
        modelAndView.addObject("userStatuses", User.Status.values());
        return modelAndView;
    }

    /**
     * save user
     * @param user user
     */
   @PostMapping("save-user")
   public void saveUser(@RequestBody User user) {
       userService.saveUser(user);
   }

}
