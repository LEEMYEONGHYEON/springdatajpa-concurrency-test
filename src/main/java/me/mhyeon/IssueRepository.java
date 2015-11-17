package me.mhyeon;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by mhyeon.lee on 2015. 11. 16..
 */
public interface IssueRepository extends JpaRepository<Issue, Long> {
}
