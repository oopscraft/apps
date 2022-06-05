package org.oopscraft.apps.batch.item.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oopscraft.apps.batch.item.file.annotation.Align;
import org.oopscraft.apps.batch.item.file.annotation.Length;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileItemVo {

    @Length(30)
    private String name;

    @Length(value=30, align= Align.RIGHT, padChar = '0')
    private Number number;

    @Length(30)
    private long longNumber;

    @Length(30)
    private double doubleNumber;

    @Length(30)
    private BigDecimal bigDecimal;

    @Length(30)
    private java.sql.Date sqlDate;

    @Length(30)
    private java.util.Date utilDate;

    @Length(30)
    private java.sql.Timestamp timestamp;

    @Length(30)
    private LocalDateTime localDateTime;

    @Length(30)
    private LocalDate localDate;

    @Length(30)
    private String data;

}
