package com.dyslexia.dyslexia.domain.pdf;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageBlockImpl extends BlockImpl implements ImageBlock {
    private String url;
    private String alt;
    private Integer width;
    private Integer height;
} 