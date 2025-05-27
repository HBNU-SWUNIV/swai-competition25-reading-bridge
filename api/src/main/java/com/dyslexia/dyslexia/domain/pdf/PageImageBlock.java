package com.dyslexia.dyslexia.domain.pdf;

public interface PageImageBlock extends Block {
    String getImageId();
    String getUrl();
    void setUrl(String url);
    void setAlt(String alt);
    void setPrompt(String prompt);
    void setConcept(String concept);
    String getAlt();
    String getPrompt();
    String getConcept();
} 