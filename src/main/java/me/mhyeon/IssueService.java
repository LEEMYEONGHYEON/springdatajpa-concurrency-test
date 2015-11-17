package me.mhyeon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by mhyeon.lee on 2015. 11. 16..
 */
@Service
@Transactional
public class IssueService {

    @Autowired
    private IssueRepository repository;

    public void updateWithDirtyChecking(Issue issue, String content,
                                        Optional<Consumer<Void>> beforeConsumer,
                                        Optional<Consumer<Void>> afterConsumer) {
        consume(beforeConsumer);

        issue = repository.findOne(issue.getId());
        if (issue == null) {
            throw new RuntimeException("Issue is not exist");
        }
        issue.setContent(content);

        consume(afterConsumer);
    }

    public void updateWithMerge(Issue issue,
                                Optional<Consumer<Void>> beforeConsumer,
                                Optional<Consumer<Void>> afterConsumer) {
        consume(beforeConsumer);

        repository.save(issue);

        consume(afterConsumer);
    }

    public void updateWithMergeIdParam(Long id, String content,
                                       Optional<Consumer<Void>> beforeConsumer,
                                       Optional<Consumer<Void>> afterConsumer) {
        consume(beforeConsumer);

        Issue issue = repository.findOne(id);
        if (issue == null) {
            throw new RuntimeException("Issue is not exist");
        }
        issue.setContent(content);
        repository.save(issue);

        consume(afterConsumer);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    private void consume(Optional<Consumer<Void>> consumer) {
        consumer.orElse(Function.identity()::apply)
                .accept(null);
    }
}
