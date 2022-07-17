package org.oopscraft.apps.batch.item.db.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;
import org.oopscraft.apps.batch.item.db.dto.DbItemBackupVo;
import org.oopscraft.apps.batch.item.db.dto.DbItemVo;

@Mapper
public interface DbItemMapper {

    public Cursor<DbItemVo> selectDbItems(@Param("limit")int limit);

    public Integer insertDbItem(DbItemVo dbItemvo);

    public Integer insertDbItemBackup(DbItemBackupVo dbItemBackup);

}
