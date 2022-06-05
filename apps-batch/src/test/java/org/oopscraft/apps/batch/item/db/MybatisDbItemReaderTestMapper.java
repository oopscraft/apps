package org.oopscraft.apps.batch.item.db;

import org.oopscraft.apps.batch.item.db.vo.DbItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

@Mapper
public interface MybatisDbItemReaderTestMapper {

    public Cursor<DbItemVo> selectItems(@Param("limit")int limit);

}
