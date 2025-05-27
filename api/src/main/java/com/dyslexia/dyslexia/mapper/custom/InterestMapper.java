package com.dyslexia.dyslexia.mapper.custom;

import com.dyslexia.dyslexia.entity.Interest;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InterestMapper {

  public List<Interest> toEntityList(List<String> names) {
    return names.stream().map(name -> Interest.builder().name(name).build()).toList();
  }

  public List<String> toStringList(List<Interest> interests) {
    return interests.stream().map(Interest::getName).toList();
  }
}
