package org.oopscraft.apps.batch.item.db.dto;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "test_db_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DbItemEntity {

    @Id
    @Column(name = "id", length=64)
    private String id;

    @Column(name = "name")
    private String name;

}

