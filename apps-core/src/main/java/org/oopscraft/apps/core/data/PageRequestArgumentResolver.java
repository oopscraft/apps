package org.oopscraft.apps.core.data;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

public class PageRequestArgumentResolver implements HandlerMethodArgumentResolver {

    private SortHandlerMethodArgumentResolver sortArgumentResolver;

    /**
     * constructor
     */
    public PageRequestArgumentResolver() {
        sortArgumentResolver = new SortHandlerMethodArgumentResolver();
        sortArgumentResolver.setSortParameter("_sort");
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PageRequest.class.equals(parameter.getParameterType());
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        int page = Optional.ofNullable(webRequest.getParameter("_page")).map(Integer::parseInt).orElse(0);
        int size = Optional.ofNullable(webRequest.getParameter("_size")).map(Integer::parseInt).orElse(100);
        Sort sort = Optional.ofNullable(webRequest.getParameter("_sort")).map(value ->{
            return sortArgumentResolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        }).orElse(Sort.unsorted());
        return new PageRequest(page, size, sort);
    }

}
