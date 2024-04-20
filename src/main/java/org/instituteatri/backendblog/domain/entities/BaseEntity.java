package org.instituteatri.backendblog.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity {

    protected String name;
    protected String slug;

    @JsonIgnore
    private int postCount;

    public BaseEntity(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}