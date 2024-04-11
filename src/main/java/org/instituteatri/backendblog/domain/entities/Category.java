package org.instituteatri.backendblog.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;
    private String slug;

    private List<Post> posts = new ArrayList<>();


    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public void UpdatedCategory(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
