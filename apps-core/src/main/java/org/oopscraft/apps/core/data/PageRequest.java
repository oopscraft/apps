package org.oopscraft.apps.core.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletResponse;

public class PageRequest extends AbstractPageRequest {

    private Sort sort;

    @Setter
    @Getter
    private long totalCount = -1;

    /**
     * constructor
     * @param page
     * @param size
     * @param sort
     */
    public PageRequest(int page, int size, Sort sort) {
        super(page, size);
        this.sort = sort;
    }

    /**
     * creates PageRequest
     * @param page
     * @param size
     * @return
     */
    public static PageRequest of(int page, int size) {
        return of(page, size, Sort.unsorted());
    }

    /**
     * creates PageRequest
     * @param page
     * @param size
     * @param sort
     * @return
     */
    public static PageRequest of(int page, int size, Sort sort) {
        return new PageRequest(page, size, sort);
    }

    /**
     * setSort
     * @param sort
     */
    public void setSort(Sort sort){
        this.sort = sort;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new PageRequest(getPageNumber() + 1, getPageSize(), getSort());
    }

    @Override
    public Pageable previous() {
        return getPageNumber() == 0 ? this : new PageRequest(getPageNumber() - 1, getPageSize(), getSort());
    }

    @Override
    public Pageable first() {
        return new PageRequest(0, getPageSize(), getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new PageRequest(pageNumber, getPageSize(), getSort());
    }

    /**
     * toPageRowBound for Mybatis
     * @return
     */
    public PageRowBounds toPageRowBounds() {
        return new PageRowBounds((int)getOffset(), this.getPageSize());
    }

    /**
     * sendTotalCount
     * @param response HttpServletResponse
     */
    public void sendTotalCount(HttpServletResponse response) {
        StringBuffer contentRange = new StringBuffer()
                .append("items")
                .append(" ")
                .append(getOffset()+1)
                .append("-")
                .append(getOffset() + getPageSize())
                .append("/")
                .append(totalCount);
        response.setHeader(HttpHeaders.CONTENT_RANGE, contentRange.toString());
    }

}
