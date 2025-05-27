package com.dyslexia.dyslexia.domain.pdf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageImageBlockImpl extends BlockImpl implements PageImageBlock {
    @JsonProperty("imageId")
    private String imageId;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("alt")
    private String alt;
    
    @JsonProperty("prompt")
    private String prompt;
    
    @JsonProperty("concept")
    private String concept;

    @Override
    public String getPromptForImage() {
        return this.prompt;
    }
} 