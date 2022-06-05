package org.oopscraft.apps.batch.item.file;

import lombok.extern.slf4j.Slf4j;
import org.oopscraft.apps.batch.BatchConfig;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class RawFileItemWriterReaderTest {

    @Test
    public void doDefaultCase() {

        // defines
        final String fileName = "doDefaultCase.txt";

        // 1. write
        List<FileItemVo> writeItems =  new ArrayList<>();
        RawFileItemWriter fileItemWriter = RawFileItemWriter.builder()
                .name("test_name")
                .filePath(BatchConfig.getDataDirectory(this) + fileName)
                .lineSeparator("\r\n")
                .build();
        try {
            fileItemWriter.open(new ExecutionContext());
            for(int i = 0; i < 123; i ++ ) {
                FileItemVo item = FileItemVo.builder()
                        .name(String.format("한글%s", i))
                        .number(i)
                        .longNumber((long)i)
                        .doubleNumber(123.456)
                        .bigDecimal(new BigDecimal(i))
                        .sqlDate(new java.sql.Date(System.currentTimeMillis()))
                        .utilDate(new Date())
                        .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                        .data(String.format("한글~~~%d",i))
                        .localDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                        .localDate(LocalDate.now())
                        .build();
                fileItemWriter.write(item.toString().getBytes());
                writeItems.add(item);
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            fileItemWriter.close();
        }

        // 2. read
        List<FileItemVo> readItems = new ArrayList<>();
        FixedLengthFileItemReader<FileItemVo> fileItemReader = FixedLengthFileItemReader.<FileItemVo>builder()
                .name("reader")
                .filePath(BatchConfig.getDataDirectory(this) + fileName)
                .lineSeparator("\r\n")
                .itemType(FileItemVo.class)
                .build();
        try {
            fileItemReader.open(new ExecutionContext());
            for(FileItemVo item = fileItemReader.read(); item != null; item = fileItemReader.read()){
                log.debug("{}", item);
                readItems.add(item);
            }
        }catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }finally{
            fileItemReader.close();
        }

        // 3. compare
        assertTrue(writeItems.size() == readItems.size());
        for(int i = 0, size = writeItems.size(); i < size; i ++ ){
            FileItemVo writeItem = writeItems.get(i);
            FileItemVo readItem = readItems.get(i);
            log.info("writeItem\t:{}", writeItem.toString());
            log.info("readItem\t:{}", readItem.toString());
            assertTrue(writeItems.get(i).toString().equals(readItems.get(i).toString()));
        }

    }

}
