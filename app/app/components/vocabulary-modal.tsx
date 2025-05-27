import React from 'react';
import { VocabularyAnalysis } from '../../types/vocabulary';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { Button } from './ui/button';
import { VolumeUp } from 'lucide-react';

interface VocabularyModalProps {
  isOpen: boolean;
  onClose: () => void;
  vocabularyData?: VocabularyAnalysis;
  onPlayAudio?: (text: string) => void;
}

export function VocabularyModal({
  isOpen,
  onClose,
  vocabularyData,
  onPlayAudio,
}: VocabularyModalProps) {
  if (!vocabularyData) return null;

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle className="flex justify-between items-center">
            <span>어려운 어휘 학습하기</span>
            <Button
              variant="ghost"
              size="icon"
              onClick={() => onPlayAudio?.(vocabularyData.word)}
            >
              <VolumeUp className="w-5 h-5" />
            </Button>
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-4">
          <div className="text-sm text-gray-500">
            총 {vocabularyData ? 1 : 0}개의 어려운 어휘가 있어요
          </div>

          <div className="p-4 bg-gray-50 rounded-lg">
            <div className="flex items-center gap-2 mb-2">
              <span className="text-lg font-semibold">{vocabularyData.word}</span>
              <span className="text-sm text-gray-500">
                ({vocabularyData.difficultyLevel})
              </span>
            </div>

            <div className="space-y-2">
              <div>
                <span className="font-medium">뜻:</span>{' '}
                {vocabularyData.definition}
              </div>
              <div>
                <span className="font-medium">쉬운 설명:</span>{' '}
                {vocabularyData.simplifiedDefinition}
              </div>
              {vocabularyData.examples && (
                <div>
                  <span className="font-medium">예문:</span>{' '}
                  {vocabularyData.examples}
                </div>
              )}
            </div>

            <div className="mt-4">
              <Button
                variant="outline"
                className="w-full"
                onClick={() => {
                  // TODO: 쓰기 연습 기능 구현
                }}
              >
                ✏️ 쓰기 연습하기
              </Button>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
} 