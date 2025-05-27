package com.dyslexia.dyslexia.domain.pdf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextBlockImpl extends BlockImpl implements TextBlock {
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("blank")
    private boolean blank;
} 