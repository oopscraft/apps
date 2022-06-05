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
public class DelimiterFileItemWriterReaderTest {

    @Test
    public void doDefaultCase() {

        // 1. write
        List<FileItemVo> writeItems =  new ArrayList<>();
        DelimiterFileItemWriter<FileItemVo> fileItemWriter = DelimiterFileItemWriter.<FileItemVo>builder()
                .name("test_name")
                .filePath(BatchConfig.getDataDirectory(this) + "doDefaultCase.tsv")
                .encoding("EUC-KR")
                .lineSeparator("\r\n")
                .itemType(FileItemVo.class)
                .build();
        try {
            fileItemWriter.open(new ExecutionContext());
            for(int i = 0; i < 100; i ++ ) {
                FileItemVo item = FileItemVo.builder()
                        .name(String.format("홍길동ㄱㄴㄷ%s", i))
                        .number(i)
                        .longNumber((long)i)
                        .doubleNumber(123.456)
                        .bigDecimal(new BigDecimal(i))
                        .sqlDate(new java.sql.Date(System.currentTimeMillis()))
                        .utilDate(new Date())
                        .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                        .data(String.format("동해물과백두산이~~~%d",i))
                        .localDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                        .localDate(LocalDate.now())
                        .build();
                fileItemWriter.write(item);

                // sub line


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
        DelimiterFileItemReader<FileItemVo> fileItemReader = DelimiterFileItemReader.<FileItemVo>builder()
                .name("reader")
                .filePath(BatchConfig.getDataDirectory(this) + "doDefaultCase.tsv")
                .encoding("EUC-KR")
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
            assertTrue(writeItem.toString().equals(readItem.toString()));
        }

    }


}
