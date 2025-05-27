import { useState } from 'react';
import { vocabularyAnalysisApi } from '../api/vocabulary-analysis';
import type { VocabularyAnalysis, VocabularyAnalysisSearchRequest } from '@/shared/api/types';

export function useVocabularyAnalysis() {
  const [data, setData] = useState<VocabularyAnalysis[] | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const searchVocabulary = async (params: VocabularyAnalysisSearchRequest) => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await vocabularyAnalysisApi.search(params);
      if (response && response.length > 0) {
        setData(response);
      } else {
        setData(null);
      }
    } catch (err) {
      setError(err instanceof Error ? err : new Error('Unknown error occurred'));
      setData(null);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    data,
    isLoading,
    error,
    searchVocabulary,
  };
} 