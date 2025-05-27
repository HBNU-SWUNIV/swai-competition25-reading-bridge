package com.dyslexia.dyslexia.domain.pdf;

import java.util.List;

public interface TableBlock extends Block {
    List<String> getHeaders();
    List<List<String>> getRows();
} 