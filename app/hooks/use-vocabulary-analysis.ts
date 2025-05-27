import { useState } from 'react';
import { VocabularyAnalysis, VocabularyAnalysisSearchRequest } from '../types/vocabulary';

export function useVocabularyAnalysis() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<VocabularyAnalysis | null>(null);

  const searchVocabulary = async (params: VocabularyAnalysisSearchRequest) => {
    try {
      setIsLoading(true);
      setError(null);

      const result = await axiosClient.post<VocabularyAnalysis>(
        '/vocabulary-analysis/search',
        params
      );

      setData(result);
      return result;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.';
      setError(errorMessage);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    searchVocabulary,
    isLoading,
    error,
    data,
  };
} 