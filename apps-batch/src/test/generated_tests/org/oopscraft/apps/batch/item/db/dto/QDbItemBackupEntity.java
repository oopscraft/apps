package org.oopscraft.apps.batch.item.db.dto;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDbItemBackupEntity is a Querydsl query type for DbItemBackupEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDbItemBackupEntity extends EntityPathBase<DbItemBackupEntity> {

    private static final long serialVersionUID = -18601713L;

    public static final QDbItemBackupEntity dbItemBackupEntity = new QDbItemBackupEntity("dbItemBackupEntity");

    public final StringPath id = createString("id");

    public final StringPath name = createString("name");

    public QDbItemBackupEntity(String variable) {
        super(DbItemBackupEntity.class, forVariable(variable));
    }

    public QDbItemBackupEntity(Path<? extends DbItemBackupEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDbItemBackupEntity(PathMetadata metadata) {
        super(DbItemBackupEntity.class, metadata);
    }

}

