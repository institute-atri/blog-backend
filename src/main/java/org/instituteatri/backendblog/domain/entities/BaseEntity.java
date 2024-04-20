package org.instituteatri.backendblog.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class BaseEntity implements Serializable {

    protected String name;
    protected String slug;

    @JsonIgnore
    private int postCount;

    public BaseEntity(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}