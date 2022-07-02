package org.oopscraft.apps.web;

import lombok.RequiredArgsConstructor;
import org.oopscraft.apps.core.user.User;
import org.oopscraft.apps.core.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Controller
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping("/")
public class WebControllerAdvice {


    private final WebConfig webConfig;

    private final UserService userService;

    /**
     * index
     * @return
     * @throws Exception
     */
    @GetMapping
    public ModelAndView index() throws Exception {
        ModelAndView modelAndView = new ModelAndView("_index.html");
        return modelAndView;
    }

    /**
     * initBinder
     * @param webDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(new Date(Long.valueOf(text)));
            }
        });
        webDataBinder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                Date date = new Date(Long.valueOf(text));
                LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                setValue(localDateTime);
            }
        });
        webDataBinder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                Date date = new Date(Long.valueOf(text));
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                setValue(localDate);
            }
        });
    }

    /**
     * Returns locale list
     * @return
     * @throws Exception
     */
    @ModelAttribute("_locales")
    public List<Locale> getLocales() throws Exception {
        List<Locale> locales = new ArrayList<>();
        List<String> configLocales = webConfig.getLocales();
        for(Locale locale : Locale.getAvailableLocales()) {
            if(configLocales.contains(locale.toString())) {
                locales.add(locale);
            }
        }
        return locales;
    }

    /**
     * Return current selected locale
     * @param request
     * @return
     * @throws Exception
     */
    @ModelAttribute("_locale")
    public Locale getLocale(HttpServletRequest request) throws Exception {
        return RequestContextUtils.getLocaleResolver(request).resolveLocale(request);
    }

    /**
     * Returns current login user
     * @return
     * @throws Exception
     */
    @ModelAttribute("_user")
    public User getUser() throws Exception {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userService.getUser(userDetails.getUsername());
        } else {
            return new User();
        }
    }

}
