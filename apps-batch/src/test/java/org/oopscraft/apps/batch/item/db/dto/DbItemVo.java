package org.oopscraft.apps.batch.item.db.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DbItemVo {

    private String id;

    private String name;

}

