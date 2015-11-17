package me.mhyeon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by mhyeon.lee on 2015. 11. 16..
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringDataJpaConcurrencyMergeTestApplication.class)
public class ConcurrencyMergeTest {

    @Autowired
    private IssueService service;

    @Autowired
    private IssueRepository repository;

    private Issue issue;

    @Before
    public void setup() {
        repository.deleteAll();

        issue = new Issue("제목", "본문");
        repository.save(issue);
    }

    /*

        Spring-Data-JPA Concurrency Save(persist or merge) Test

        If update and delete requests are called in same time, result is deleted.
        but One case's result is different.

     */

    // Update with Dirty Checking : Merge First
    @Test
    public void concurrencyDirtyCheckTest_MergeFirst() {
        final Long id = issue.getId();

        runTaskWithConcurrencyDelete(id,
                () -> service.updateWithDirtyChecking(issue, "본문수정",
                        Optional.empty(),
                        Optional.of(n -> sleep()))
        );

        assertNull(repository.findOne(id));
        assertThat(repository.findAll().size(), is(0));
    }

    // Update with save(merge) : Merge First
    @Test
    public void concurrencyMergeTest_MergeFirst() {
        final Long id = issue.getId();

        runTaskWithConcurrencyDelete(id,
                () -> {
                    issue.setContent("본문수정");
                    service.updateWithMerge(issue,
                            Optional.empty(),
                            Optional.of(n -> sleep()));
                }
        );

        assertNull(repository.findOne(id));
        assertThat(repository.findAll().size(), is(0));
    }


    // Update with Dirty Checking : Delete First
    @Test
    public void concurrencyDirtyCheckTest_DeleteFirst() {
        final Long id = issue.getId();

        runTaskWithConcurrencyDelete(id,
                () -> service.updateWithDirtyChecking(issue, "본문수정",
                        Optional.empty(),
                        Optional.of(n -> sleep()))
        );

        assertNull(repository.findOne(id));
        assertThat(repository.findAll().size(), is(0));
    }

    // Update with save(merge) : Delete First
    // It would be failed.
    @Test
    public void concurrencyMergeTest_DeleteFirst() {
        final Long id = issue.getId();

        runTaskWithConcurrencyDelete(id,
                () -> {
                    issue.setContent("본문수정");
                    service.updateWithMerge(issue,
                            Optional.of(n -> sleep()),
                            Optional.empty());
                });

        assertNull(repository.findOne(id));
        assertThat(repository.findAll().size(), is(0)); // fail
    }

    // Update with save(merge) but param is Id : Merge First
    @Test
    public void concurrencyMergeIdParamTest_MergeFirst() {
        final Long id = issue.getId();

        runTaskWithConcurrencyDelete(id,
                () -> service.updateWithMergeIdParam(id, "본문수정",
                        Optional.empty(),
                        Optional.of(n -> sleep()))
        );

        assertNull(repository.findOne(id));
        assertThat(repository.findAll().size(), is(0));
    }

    // Update with save(merge) but param is Id : Delete First
    @Test
    public void concurrencyMergeIdParamTest_DeleteFirst() {
        final Long id = issue.getId();

        runTaskWithConcurrencyDelete(id,
                () -> service.updateWithMergeIdParam(id, "본문수정",
                        Optional.of(n -> sleep()),
                        Optional.empty())
        );

        assertNull(repository.findOne(id));
        assertThat(repository.findAll().size(), is(0));
    }

    public void runTaskWithConcurrencyDelete(Long id, Runnable updateTask) {

        new Thread(updateTask)
                .start();

        new Thread(() -> service.delete(id))
                .start();

        sleepLongTerm();
    }

    private void sleep() {
        sleep(1000);
    }

    private void sleepLongTerm() {
        sleep(2000);
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
