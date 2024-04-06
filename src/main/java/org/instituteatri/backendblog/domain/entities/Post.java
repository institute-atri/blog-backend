package org.instituteatri.backendblog.domain.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Post{
    private String id;
    private String title;
    private String body;
    private Date date;
    private User author;
    private List<Comment> comments = new ArrayList<>();
}
