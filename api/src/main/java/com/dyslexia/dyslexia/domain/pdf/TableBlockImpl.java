package com.dyslexia.dyslexia.domain.pdf;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TableBlockImpl extends BlockImpl implements TableBlock {
    private List<String> headers;
    private List<List<String>> rows;
} 