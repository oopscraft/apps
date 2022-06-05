package org.oopscraft.apps.batch.item.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oopscraft.apps.batch.item.file.annotation.Align;
import org.oopscraft.apps.batch.item.file.annotation.Length;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileChildItemVo {

    @Length(10)
    private String name;

    @Length(value=10, align= Align.RIGHT, padChar = '0')
    private Number number;

    @Length(30)
    private LocalDateTime localDateTime;

}
