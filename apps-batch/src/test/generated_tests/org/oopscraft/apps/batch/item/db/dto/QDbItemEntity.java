package org.oopscraft.apps.batch.item.db.dto;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDbItemEntity is a Querydsl query type for DbItemEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDbItemEntity extends EntityPathBase<DbItemEntity> {

    private static final long serialVersionUID = 1053392941L;

    public static final QDbItemEntity dbItemEntity = new QDbItemEntity("dbItemEntity");

    public final StringPath id = createString("id");

    public final StringPath name = createString("name");

    public QDbItemEntity(String variable) {
        super(DbItemEntity.class, forVariable(variable));
    }

    public QDbItemEntity(Path<? extends DbItemEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDbItemEntity(PathMetadata metadata) {
        super(DbItemEntity.class, metadata);
    }

}

