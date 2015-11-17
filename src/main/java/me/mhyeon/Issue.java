package me.mhyeon;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by mhyeon.lee on 2015. 11. 16..
 */
@Entity
@Data
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    protected Issue() {
    }

    public Issue(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdTime = new Date();
    }
}
