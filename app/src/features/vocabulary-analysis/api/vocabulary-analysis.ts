import { axiosClient } from '@/shared/api/axios';
import type { VocabularyAnalysis, VocabularyAnalysisSearchRequest } from '@/shared/api/types/vocabulary';

export const vocabularyAnalysisApi = {
  search: (params: VocabularyAnalysisSearchRequest) => {
    return axiosClient.post('/vocabulary-analysis/search', params) as unknown as VocabularyAnalysis[];
  },
}; 