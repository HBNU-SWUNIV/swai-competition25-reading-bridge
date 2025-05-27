package com.dyslexia.dyslexia.entity;

import com.dyslexia.dyslexia.enums.ColorScheme;
import com.dyslexia.dyslexia.enums.Grade;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String clientId;

  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "teacherId")
  private Teacher teacher;

  @Enumerated(EnumType.STRING)
  private Grade grade;

  private String type;
  private String state;
  private String profileImageUrl;
  
  @Column(name = "default_font_size")
  private Integer defaultFontSize = 16;
  
  @Column(name = "default_line_spacing")
  private Float defaultLineSpacing = 1.5f;
  
  @Column(name = "default_letter_spacing")
  private Float defaultLetterSpacing = 0.1f;
  
  @Enumerated(EnumType.STRING)
  @Column(name = "default_color_scheme")
  private ColorScheme defaultColorScheme = ColorScheme.LIGHT;
  
  @Column(name = "default_text_to_speech_enabled")
  private Boolean defaultTextToSpeechEnabled = false;
  
  @Column(name = "default_reading_highlight_enabled")
  private Boolean defaultReadingHighlightEnabled = true;
  
  @Column(name = "default_background_color", length = 30)
  private String defaultBackgroundColor = "#FFFFFF";
  
  @Column(name = "default_text_color", length = 30)
  private String defaultTextColor = "#000000";

  @ManyToMany
  @JoinTable(name = "student_interest", joinColumns = @JoinColumn(name = "studentId"), inverseJoinColumns = @JoinColumn(name = "interestId"))
  private List<Interest> interests = new ArrayList<>();

  @Builder
  public Student(String clientId, String name, Teacher teacher, Grade grade, String type, String state,
      String profileImageUrl, List<Interest> interests, Integer defaultFontSize, Float defaultLineSpacing,
      Float defaultLetterSpacing, ColorScheme defaultColorScheme, Boolean defaultTextToSpeechEnabled,
      Boolean defaultReadingHighlightEnabled, String defaultBackgroundColor, String defaultTextColor) {
    this.clientId = clientId;
    this.name = name;
    this.teacher = teacher;
    this.grade = grade;
    this.type = type;
    this.state = state;
    this.profileImageUrl = profileImageUrl;
    this.interests = interests != null ? interests : new ArrayList<>();
    this.defaultFontSize = defaultFontSize != null ? defaultFontSize : 16;
    this.defaultLineSpacing = defaultLineSpacing != null ? defaultLineSpacing : 1.5f;
    this.defaultLetterSpacing = defaultLetterSpacing != null ? defaultLetterSpacing : 0.1f;
    this.defaultColorScheme = defaultColorScheme != null ? defaultColorScheme : ColorScheme.LIGHT;
    this.defaultTextToSpeechEnabled = defaultTextToSpeechEnabled != null ? defaultTextToSpeechEnabled : false;
    this.defaultReadingHighlightEnabled = defaultReadingHighlightEnabled != null ? defaultReadingHighlightEnabled : true;
    this.defaultBackgroundColor = defaultBackgroundColor != null ? defaultBackgroundColor : "#FFFFFF";
    this.defaultTextColor = defaultTextColor != null ? defaultTextColor : "#000000";
  }

  public void setTeacher(Teacher teacher) {
    this.teacher = teacher;

    if (teacher != null && !teacher.getStudents().contains(this)) {
      teacher.getStudents().add(this);
    }
  }

  public void addInterests(List<Interest> interests) {
    if (this.interests == null) {
      this.interests = new ArrayList<>();
    }

    for (Interest interest : interests) {
      if (!this.interests.contains(interest)) {
        this.interests.add(interest);
      }
    }
  }
  
  public void updateAccessibilitySettings(Integer fontSize, Float lineSpacing, Float letterSpacing, 
      ColorScheme colorScheme, Boolean textToSpeechEnabled, Boolean readingHighlightEnabled,
      String backgroundColor, String textColor) {
    if (fontSize != null) {
      this.defaultFontSize = fontSize;
    }
    if (lineSpacing != null) {
      this.defaultLineSpacing = lineSpacing;
    }
    if (letterSpacing != null) {
      this.defaultLetterSpacing = letterSpacing;
    }
    if (colorScheme != null) {
      this.defaultColorScheme = colorScheme;
    }
    if (textToSpeechEnabled != null) {
      this.defaultTextToSpeechEnabled = textToSpeechEnabled;
    }
    if (readingHighlightEnabled != null) {
      this.defaultReadingHighlightEnabled = readingHighlightEnabled;
    }
    if (backgroundColor != null) {
      this.defaultBackgroundColor = backgroundColor;
    }
    if (textColor != null) {
      this.defaultTextColor = textColor;
    }
  }
}
