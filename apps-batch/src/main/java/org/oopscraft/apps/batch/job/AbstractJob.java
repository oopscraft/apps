package org.oopscraft.apps.batch.job;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Getter
public abstract class AbstractJob extends SimpleJob implements InitializingBean {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    public abstract void initialize();

    @Override
    public final void afterPropertiesSet() {
        this.setName(this.getClass().getName());
        this.setJobRepository(jobRepository);
        this.initialize();
    }

}
