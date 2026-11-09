package com.santea.admin;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("sequences")
public class AdminSequence {
    @Id
    private String id;
    private long value;

    public AdminSequence() {}

    public AdminSequence(String id, long value) {
        this.id = id;
        this.value = value;
    }

    public long getValue() {
        return value;
    }
}
