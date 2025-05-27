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

export interface VocabularyAnalysisSearchRequest {
  documentId: number;
  pageNumber?: number;
  blockId?: string;
} 