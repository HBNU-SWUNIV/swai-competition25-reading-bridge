package com.dyslexia.dyslexia.domain.pdf;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListBlockImpl extends BlockImpl implements ListBlock {
    private List<String> items;
} 