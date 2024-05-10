package org.instituteatri.backendblog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagResponseDTO {
    private String id;
    private String name;
    private String slug;
}
