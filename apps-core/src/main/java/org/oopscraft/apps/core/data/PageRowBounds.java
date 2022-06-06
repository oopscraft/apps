package org.oopscraft.apps.core.data;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.session.RowBounds;

public class PageRowBounds extends RowBounds {

    @Setter
    @Getter
    private int offset;

    @Setter
    @Getter
    private int limit;

    @Setter
    @Getter
    private int totalCount = -1;

    /**
     * PageRowBounds
     * @param offset
     * @param limit
     */
    public PageRowBounds(int offset, int limit) {
        super(offset, limit);
        this.offset = offset;
        this.limit = limit;
    }

}
