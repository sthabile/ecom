package com.pc.ecom.Payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private List<CategoryDTO> content;

    //I mean... we could... but let's comment this out for now ;)
//    private String status;
//
//    public void addToContent(CategoryDTO categoryDTO) {
//        if (this.content == null) {
//            this.content = new ArrayList<>();
//        }
//        content.add(categoryDTO);
//    }
}
