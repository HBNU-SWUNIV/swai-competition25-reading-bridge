import React from 'react';
import type { VocabularyAnalysis, PhonemeAnalysis } from '@/shared/api/types';
import { Button } from '@/shared/ui/button';
import { Badge } from '@/shared/ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/shared/ui/tabs';
import { Pencil, ChevronLeft, ChevronRight } from 'lucide-react';
import { SoundButton } from '@/shared/ui/sound-button';
import { QuoteCard } from './quote-card';
import { DefinitionSection } from './definition-section';
import { SyllableAnalysis } from './syllable-analysis';
import { WritingSteps } from './writing-steps';
import { LearningTipsSection } from './learning-tips-section';
import { toast } from 'sonner';

interface VocaAnalysisProps {
	currentVocabulary: VocabularyAnalysis;
	currentIndex: number;
	totalCount: number;
	onPrevious: () => void;
	onNext: () => void;
	onWritingStart: () => void;
	onSpeak: (word: string) => void;
}

export function VocaAnalysis({
	currentVocabulary,
	currentIndex,
	totalCount,
	onPrevious,
	onNext,
	onWritingStart,
	onSpeak,
}: VocaAnalysisProps) {
	const [isDefinitionOpen, setIsDefinitionOpen] = React.useState(true);
	const [hasTabInteracted, setHasTabInteracted] = React.useState(false);

	const phonemeAnalysis = currentVocabulary.phonemeAnalysisJson
		? (JSON.parse(currentVocabulary.phonemeAnalysisJson) as PhonemeAnalysis)
		: null;

	const handleTabClick = () => {
		if (!hasTabInteracted) {
			setIsDefinitionOpen(false);
			setHasTabInteracted(true);
		}
	};

	if (!phonemeAnalysis || ((phonemeAnalysis as any)?.error)) {
		return null;
	}

	return (
		<div className="space-y-4 flex flex-col h-full mt-6">
			<div className="flex items-center justify-between">
				<div className="flex items-center gap-4">
					<h3 className="text-2xl font-bold">{currentVocabulary.word}</h3>
					<Badge className={getDifficultyStyle(currentVocabulary.difficultyLevel)}>
						{getDifficultyText(currentVocabulary.difficultyLevel)}
					</Badge>
					<SoundButton text={currentVocabulary.word} onSpeak={onSpeak} />
					<span className="text-sm text-gray-500">
						{currentIndex + 1} / {totalCount}
					</span>
				</div>
				<Button variant="outline" size="sm" onClick={onWritingStart}>
					<Pencil className="mr-2 h-4 w-4" />
					쓰기 연습
				</Button>
			</div>

			<QuoteCard text={currentVocabulary.originalSentence || "문장이 없습니다."} />

			<DefinitionSection 
				word={currentVocabulary.word}
				definition={currentVocabulary.definition}
				simplifiedDefinition={currentVocabulary.simplifiedDefinition}
				examples={currentVocabulary.examples}
				isOpen={isDefinitionOpen}
				onOpenChange={setIsDefinitionOpen}
			/>

			<Tabs defaultValue="phoneme" className="w-full flex-1 overflow-y-scroll" onClick={handleTabClick}>
				<TabsList className="w-full">
					<TabsTrigger value="phoneme" className="flex-1">음소 분석</TabsTrigger>
					<TabsTrigger value="writing" className="flex-1">쓰기 순서</TabsTrigger>
					<TabsTrigger value="tips" className="flex-1">학습 팁</TabsTrigger>
				</TabsList>

				<div className="overflow-y-auto pr-4">
					<TabsContent value="phoneme" className="mt-4">
						<div className="space-y-6">
							{phonemeAnalysis.syllables.map((syllable, index) => (
								<SyllableAnalysis 
									key={`syllable-${// biome-ignore lint/suspicious/noArrayIndexKey: <explanation>
index}`} 
									syllable={syllable} 
								/>
							))}
						</div>
					</TabsContent>

					<TabsContent value="writing" className="mt-4">
						<WritingSteps steps={phonemeAnalysis.writingOrder} />
					</TabsContent>

					<TabsContent value="tips" className="mt-4">
						<LearningTipsSection tips={phonemeAnalysis.learningTips} />
					</TabsContent>
				</div>
			</Tabs>

			<div className="fixed left-4 top-1/2 -translate-y-1/2">
				<Button
					variant="ghost"
					size="icon"
					className="h-12 w-12 rounded-full bg-white/80 shadow-md hover:bg-white"
					onClick={onPrevious}
				>
					<ChevronLeft className="h-8 w-8" />
				</Button>
			</div>

			<div className="fixed right-4 top-1/2 -translate-y-1/2">
				<Button
					variant="ghost"
					size="icon"
					className="h-12 w-12 rounded-full bg-white/80 shadow-md hover:bg-white"
					onClick={onNext}
				>
					<ChevronRight className="h-8 w-8" />
				</Button>
			</div>
		</div>
	);
}

const getDifficultyText = (level: string) => {
	switch (level) {
		case 'easy':
			return '쉬움';
		case 'medium':
			return '보통';
		default:
			return '어려움';
	}
};

const getDifficultyStyle = (level: string) => {
	switch (level) {
		case 'easy':
			return 'bg-green-100 text-green-800';
		case 'medium':
			return 'bg-yellow-100 text-yellow-800';
		default:
			return 'bg-red-100 text-red-800';
	}
}; 