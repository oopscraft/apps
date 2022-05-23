package org.oopscraft.apps.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AbstractJob extends SimpleJob implements InitializingBean {

    @Autowired
    private JobRepository jobRepository;

    public void initialize() {
        log.info("++++++++++++++++++++++++++");
    }

    @Override
    public final void afterPropertiesSet() {
        this.setName(this.getClass().getName());
        this.setJobRepository(jobRepository);
        this.initialize();
    }

}
