package com.wiltech.fileupload.files;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFile {
    private String id;
    private String name;
    private String url;
    private String type;
    private long size;
}
