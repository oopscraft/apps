package org.oopscraft.apps.web.view.admin;

import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.data.PageRequest;
import org.oopscraft.apps.core.user.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("admin/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final RoleService roleService;

    private final AuthorityService authorityService;

    /**
     * index
     * @return
     * @throws Exception
     */
    @GetMapping()
    public ModelAndView index() throws Exception {
        ModelAndView modelAndView = new ModelAndView("admin/user.html");
        modelAndView.addObject("userTypes", User.Type.values());
        modelAndView.addObject("userStatuses", User.Status.values());
        return modelAndView;
    }

    /**
     * getUsers
     * @param user
     * @param pageRequest
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "get-users")
    @ResponseBody
    public List<User> getUsers(UserSearch userSearch, PageRequest pageRequest, HttpServletResponse response) throws Exception {
        List<User> users = userService.getUsers(userSearch, pageRequest);
        pageRequest.sendTotalCount(response);
        return users;
    }

    /**
     * getUser
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping(value="get-user")
    @ResponseBody
    public User getUser(@RequestParam("id") String id) throws Exception {
        return userService.getUser(id);
    }

    /**
     * saveUser
     * @param user
     * @throws Exception
     */
    @PostMapping(value="save-user")
    @ResponseBody
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN_USER_EDIT')")
    public void saveUser(@RequestBody User user) throws Exception {
        userService.saveUser(user);
    }

    /**
     * deleteUser
     * @param id
     * @throws Exception
     */
    @DeleteMapping(value = "delete-user")
    @ResponseBody
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN_USER_EDIT')")
    public void deleteUser(@RequestParam("id")String id) throws Exception {
        userService.deleteUser(id);
    }

    /**
     * changePassword
     * @param id
     * @param password
     * @throws Exception
     */
    @PutMapping(value="change-password")
    @ResponseBody
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN_USER_EDIT')")
    public void changePassword(@RequestParam("id")String id, @RequestParam("password")String password) throws Exception {
        userService.changePassword(id, password);
    }

    /**
     * roles
     * @param role
     * @param pageRequest
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping(value = "get-roles")
    @ResponseBody
    public List<Role> getRoles(RoleSearch roleSearch, PageRequest pageRequest, HttpServletResponse response) throws Exception {
        List<Role> roles = roleService.getRoles(roleSearch, pageRequest);
        pageRequest.sendTotalCount(response);
        return roles;
    }

    /**
     * getRole
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping(value = "get-role")
    @ResponseBody
    public Role getRole(@RequestParam("id") String id) throws Exception {
        return roleService.getRole(id);
    }

    /**
     * saveRole
     * @param role
     * @throws Exception
     */
    @PostMapping(value = "save-role")
    @ResponseBody
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN_USER_EDIT')")
    public void saveRole(@RequestBody Role role) throws Exception {
        roleService.saveRole(role);
    }

    /**
     * Deletes role
     * @param role
     * @throws Exception
     */
    @DeleteMapping(value = "delete-role")
    @ResponseBody
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN_USER_EDIT')")
    public void deleteRole(@RequestParam("id")String id) throws Exception {
        roleService.deleteRole(id);
    }

    @GetMapping(value = "get-authorities")
    @ResponseBody
    public List<Authority> getAuthorities(AuthoritySearch authoritySearch, PageRequest pageRequest, HttpServletResponse response) throws Exception {
        List<Authority> authorities = authorityService.getAuthorities(authoritySearch, pageRequest);
        pageRequest.sendTotalCount(response);
        return authorities;
    }

    /**
     * getAuthority
     * @param id
     * @return
     * @throws Exception
     */
    @GetMapping(value = "get-authority")
    @ResponseBody
    public Authority getAuthority(@RequestParam("id") String id) throws Exception {
        return authorityService.getAuthority(id);
    }

    /**
     * saveRole
     * @param authority
     * @throws Exception
     */
    @PostMapping(value = "save-authority")
    @ResponseBody
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN_USER_EDIT')")
    public void saveAuthority(@RequestBody Authority authority) throws Exception {
        authorityService.saveAuthority(authority);
    }

    /**
     * Deletes role
     * @param id
     * @throws Exception
     */
    @DeleteMapping(value = "delete-authority")
    @ResponseBody
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN_USER_EDIT')")
    public void deleteAuthority(@RequestParam("id")String id) throws Exception {
        authorityService.deleteAuthority(id);
    }

}
