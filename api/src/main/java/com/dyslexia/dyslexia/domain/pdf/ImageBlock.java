package com.dyslexia.dyslexia.domain.pdf;

public interface ImageBlock extends Block {
    String getUrl();
    String getAlt();
    Integer getWidth();
    Integer getHeight();
} 