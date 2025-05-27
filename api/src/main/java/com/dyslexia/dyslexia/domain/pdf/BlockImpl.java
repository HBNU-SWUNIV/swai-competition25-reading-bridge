package com.dyslexia.dyslexia.domain.pdf;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Data;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TextBlockImpl.class, name = "TEXT"),
    @JsonSubTypes.Type(value = TextBlockImpl.class, name = "HEADING1"),
    @JsonSubTypes.Type(value = TextBlockImpl.class, name = "HEADING2"),
    @JsonSubTypes.Type(value = TextBlockImpl.class, name = "HEADING3"),
    @JsonSubTypes.Type(value = ListBlockImpl.class, name = "LIST"),
    @JsonSubTypes.Type(value = ListBlockImpl.class, name = "DOTTED"),
    @JsonSubTypes.Type(value = TableBlockImpl.class, name = "TABLE"),
    @JsonSubTypes.Type(value = PageTipBlockImpl.class, name = "PAGE_TIP"),
    @JsonSubTypes.Type(value = PageImageBlockImpl.class, name = "PAGE_IMAGE")
})
public abstract class BlockImpl implements Block {
    private static final Logger log = LoggerFactory.getLogger(BlockImpl.class);

    @JsonProperty("id")
    private String id;
    
    @JsonProperty("type")
    private String type;

    @JsonProperty("text")
    private String text;

    @JsonProperty("description")
    private String description;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("url")
    private String url;

    @JsonProperty("items")
    private List<String> items;

    @JsonProperty("headers")
    private List<String> headers;

    @JsonProperty("rows")
    private List<List<String>> rows;

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("alt")
    private String alt;

    @JsonProperty("concept")
    private String concept;

    @JsonProperty("tipId")
    private String tipId;

    @JsonProperty("imageId")
    private String imageId;

    @Override
    public String getId() { 
        return id; 
    }
    
    @Override
    public BlockType getType() { 
        if (type == null) {
            log.warn("Block type is null, id: {}, trying to infer type from class", id);
            // 클래스 이름에서 타입을 추론
            String className = this.getClass().getSimpleName();
            if (className.endsWith("BlockImpl")) {
                String inferredType = className.substring(0, className.length() - "BlockImpl".length()).toUpperCase();
                try {
                    return BlockType.valueOf(inferredType);
                } catch (IllegalArgumentException e) {
                    log.error("Failed to infer block type from class name: {}", className);
                    throw new IllegalStateException("Block type cannot be null and could not be inferred");
                }
            }
            throw new IllegalStateException("Block type cannot be null and could not be inferred");
        }
        try {
            return BlockType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid block type: {}, id: {}", type, id);
            throw new IllegalStateException("Invalid block type: " + type);
        }
    }

    public void setType(String type) {
        this.type = type != null ? type.toUpperCase() : null;
    }

    public String getPromptForImage() {
        return prompt != null ? prompt : description != null ? description : text;
    }
} 