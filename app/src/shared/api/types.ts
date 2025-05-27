export interface VocabularyAnalysisSearchRequest {
  documentId: number;
  pageNumber?: number;
  blockId?: string;
}

export interface VocabularyAnalysis {
  id: number;
  documentId: number;
  pageNumber: number;
  blockId: string;
  word: string;
  startIndex: number;
  endIndex: number;
  definition: string;
  simplifiedDefinition: string;
  examples: string;
  difficultyLevel: string;
  reason: string;
  gradeLevel: number;
  phonemeAnalysisJson: string;
  createdAt: string;
  originalSentence: string;
}

export interface PhonemeComponent {
  consonant: string;
  pronunciation: string;
  sound: string;
  writingOrder: number;
  strokes: number;
  difficulty: 'easy' | 'medium' | 'hard';
}

export interface SyllableComponents {
  initial: PhonemeComponent;
  medial: {
    vowel: string;
    pronunciation: string;
    sound: string;
    writingOrder: number;
    strokes: number;
    difficulty: 'easy' | 'medium' | 'hard';
  };
  final?: PhonemeComponent;
}

export interface SyllableInfo {
  character: string;
  pronunciation: string;
  difficulty?: number;
  syllable: string;
  writingTips: string;
  examples?: string[];
  components: {
    initial?: {
      consonant: string;
      pronunciation: string;
    };
    medial?: {
      vowel: string;
      pronunciation: string;
    };
    final?: {
      consonant: string;
      pronunciation: string;
    };
  };
}

export interface WritingStep {
  step: number;
  phoneme: string;
  syllable: string;
}

export interface LearningTips {
  commonMistakes: string[];
  practiceWords: string[];
  rhymingWords: string[];
}

export interface PhonemeAnalysis {
  syllables: SyllableInfo[];
} 