package org.oopscraft.apps.batch.item;

import lombok.Builder;
import lombok.Singular;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.classify.Classifier;
import org.springframework.classify.ClassifierSupport;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
public class ClassifierItemWriter<T> implements ItemStreamWriter<T>, InitializingBean {

    @Singular("delegate")
    private List<ItemWriter<? super T>> delegates;

    @Builder.Default
    private Classifier<T, ItemWriter<? super T>> classifier = new ClassifierSupport<>(null);

    @Builder.Default
    private boolean ignoreItemStream = false;

    public void setDelegates(List<ItemWriter<? super T>> delegates) {
        this.delegates = delegates;
    }

    public void setClassifier(Classifier<T, ItemWriter<? super T>> classifier) {
        Assert.notNull(classifier, "A classifier is required.");
        this.classifier = classifier;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {

        Map<ItemWriter<? super T>, List<T>> map = new LinkedHashMap<>();

        for (T item : items) {
            ItemWriter<? super T> key = classifier.classify(item);
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(item);
        }

        for (ItemWriter<? super T> writer : map.keySet()) {
            writer.write(map.get(writer));
        }

    }


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        for (ItemWriter<? super T> writer : delegates) {
            if (!ignoreItemStream && (writer instanceof ItemStream)) {
                ((ItemStream) writer).open(executionContext);
            }
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        for (ItemWriter<? super T> writer : delegates) {
            if (!ignoreItemStream && (writer instanceof ItemStream)) {
                ((ItemStream) writer).update(executionContext);
            }
        }
    }

    @Override
    public void close() throws ItemStreamException {
        for (ItemWriter<? super T> writer : delegates) {
            if (!ignoreItemStream && (writer instanceof ItemStream)) {
                ((ItemStream) writer).close();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(delegates, "The 'delegates' may not be null");
        Assert.notEmpty(delegates, "The 'delegates' may not be empty");
    }
}