import { useState, useRef } from 'react';
import type { VocabularyAnalysis, PhonemeComponent, SyllableComponents, SyllableInfo, WritingStep, LearningTips } from '@/shared/api/types';
import {
	Dialog,
	DialogContent,
	DialogHeader,
	DialogTitle,
	DialogFooter,
} from '@/shared/ui/dialog';
import { Button } from '@/shared/ui/button';
import { Quote, ChevronDown } from 'lucide-react';
import { Badge } from '@/shared/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/ui/card';
import { useTextToSpeech } from '@/shared/hooks/use-text-to-speech';
import {
	Collapsible,
	CollapsibleContent,
	CollapsibleTrigger,
} from "@/shared/ui/collapsible"
import { VocaAnalysis } from './voca-analysis';
import { WritingPractice } from './writing-practice';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';

interface VocabularyModalProps {
	isOpen: boolean;
	onClose: () => void;
	vocabularyList?: VocabularyAnalysis[];
}

function PhonemeCard({ component, type }: { component: PhonemeComponent | SyllableComponents['medial'] | null; type: 'initial' | 'medial' | 'final' }) {
	if (!component) {
		return (
			<div className="flex flex-col items-center gap-2 rounded-lg border border-dashed p-3 text-center">
				<div className="text-2xl font-bold text-muted-foreground">-</div>
				<div className="text-sm text-muted-foreground">{type === 'final' ? '받침' : type === 'medial' ? '모음' : '자음'}이 없어요</div>
			</div>
		);
	}

	return (
		<div className="flex flex-col items-center gap-2 rounded-lg border p-3 text-center">
			<div className="text-2xl font-bold">{type === 'medial' ? (component as SyllableComponents['medial']).vowel : (component as PhonemeComponent).consonant}</div>
			<div className="text-sm text-gray-500">{component.pronunciation}</div>
			<div className="text-xs text-gray-400">{component.sound}</div>
			<Badge className={getDifficultyStyle(component.difficulty)}>
				{getDifficultyText(component.difficulty)}
			</Badge>
		</div>
	);
}


function LearningTipsSection({ tips }: { tips: LearningTips }) {
	return (
		<div className="space-y-4">
			<div>
				<h4 className="mb-2 font-semibold">자주하는 실수</h4>
				<ul className="list-disc pl-5 text-sm text-gray-600">
					{tips.commonMistakes.map((mistake) => (
						<li key={mistake}>{mistake}</li>
					))}
				</ul>
			</div>
			<div>
				<h4 className="mb-2 font-semibold">연습할 단어</h4>
				<div className="flex flex-wrap gap-2">
					{tips.practiceWords.map((word) => (
						<Badge key={word} className="bg-blue-100 text-blue-800">{word}</Badge>
					))}
				</div>
			</div>
			<div>
				<h4 className="mb-2 font-semibold">비슷한 발음의 단어</h4>
				<div className="flex flex-wrap gap-2">
					{tips.rhymingWords.map((word) => (
						<Badge key={word} className="border border-gray-200 bg-white text-gray-800">{word}</Badge>
					))}
				</div>
			</div>
		</div>
	);
}

function QuoteCard({ text }: { text: string }) {
	return (
		<Card className="mb-4 bg-muted/50">
			<CardContent className="">
				<div className="flex gap-2">
					<Quote className="h-6 w-6 text-muted-foreground" />
					<p className="text-muted-foreground italic">{text}</p>
				</div>
			</CardContent>
		</Card>
	);
}

function DefinitionSection({ 
	word, 
	definition, 
	simplifiedDefinition, 
	examples, 
	isOpen,
	onOpenChange
}: { 
	word: string;
	definition: string;
	simplifiedDefinition: string;
	examples: string;
	isOpen: boolean;
	onOpenChange: (open: boolean) => void;
}) {
	const exampleList = examples.replace(/[\[\]"]/g, '').split(',').map(example => example.trim());

	return (
		<div className="space-y-4">
			<Card>
				<Collapsible open={isOpen} onOpenChange={onOpenChange}>
					<CardHeader className="pb-0">
						<div className="flex items-center justify-between">
							<CardTitle className="text-lg">쉽게 풀어보는 "{word}"</CardTitle>
							<CollapsibleTrigger asChild>
								<Button variant="ghost" size="sm" className="h-8 w-8 p-0">
									<ChevronDown className={`h-4 w-4 transition-transform duration-200 ${isOpen ? "" : "-rotate-90"}`} />
								</Button>
							</CollapsibleTrigger>
						</div>
					</CardHeader>
					<CollapsibleContent>
						<CardContent className="space-y-4 pt-4">
							<div>
								<h4 className="mb-2 font-semibold">의미</h4>
								<p className="text-muted-foreground">{simplifiedDefinition}</p>
							</div>
							<div>
								<h4 className="mb-2 font-semibold">자세한 설명</h4>
								<p className="text-sm text-muted-foreground">{definition}</p>
							</div>
							<div>
								<h4 className="mb-2 font-semibold">이렇게 사용해요!</h4>
								<div className="space-y-2">
									{exampleList.map((example, index) => (
										<div 
											// biome-ignore lint/suspicious/noArrayIndexKey: <explanation>
											key={index} 
											className="rounded-lg bg-amber-50 p-4"
										>
											<p className="text-orange-700">{example}</p>
										</div>
									))}
								</div>
							</div>
						</CardContent>
					</CollapsibleContent>
				</Collapsible>
			</Card>
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

export function VocabularyModal({
	isOpen,
	onClose,
	vocabularyList = [],
}: VocabularyModalProps) {
	const [currentIndex, setCurrentIndex] = useState(0);
	const { speak } = useTextToSpeech();
	const [isWritingMode, setIsWritingMode] = useState(false);
	const toastTimeoutRef = useRef<NodeJS.Timeout | null>(null);
	
	if (!vocabularyList.length) return null;

	const currentVocabulary = vocabularyList[currentIndex];
	const phonemeAnalysis = currentVocabulary.phonemeAnalysisJson
		? JSON.parse(currentVocabulary.phonemeAnalysisJson)
		: null;

    console.log({ phonemeAnalysis, currentVocabulary });

	const handlePrevious = () => {
		setCurrentIndex((prev) => (prev > 0 ? prev - 1 : vocabularyList.length - 1));
	};

	const handleNext = () => {
		setCurrentIndex((prev) => (prev < vocabularyList.length - 1 ? prev + 1 : 0));
	};

	const handleWritingStart = () => {
		setIsWritingMode(true);
	};

	const handleWritingBack = () => {
		setIsWritingMode(false);
	};

	const showDebouncedToast = () => {
		if (toastTimeoutRef.current) return;
		toastTimeoutRef.current = setTimeout(() => {
			toast.error('해당 문장에 분석된 어휘가 없어요.', {
				description: '다른 문장을 선택해주세요',
				position: 'top-center',
				duration: 1000,
			});
			toastTimeoutRef.current = null;
		}, 1000);
	};

	if (!phonemeAnalysis || (typeof phonemeAnalysis === 'object' && 'error' in phonemeAnalysis)) {
		showDebouncedToast();
		return null;
	}

	return (
		<Dialog
      open={isOpen}
      onOpenChange={onClose}
    >
			<DialogContent 
        className={cn([
          'max-w-screen-xl h-[95vh] max-h-[95vh] w-[95vw]',
          'gap-0'
        ])}
      >
				<DialogHeader className="pb-0">
					<DialogTitle className="flex items-center justify-between">
						<div className="flex items-center gap-4">
							<span>{isWritingMode ? '쓰기 연습' : '어휘 분석'}</span>
							{!isWritingMode && (
								<Badge className="bg-blue-100 text-blue-800">
									{vocabularyList.length}개의 단어
								</Badge>
							)}
						</div>
					</DialogTitle>
				</DialogHeader>

				<div className="relative flex-1 flex flex-col h-full overflow-y-hidden">
					<div className="px-16 overflow-y-hidden flex-1">
						{isWritingMode ? (
							<WritingPractice
								word={currentVocabulary.word}
								phonemeAnalysis={phonemeAnalysis}
								onBack={handleWritingBack}
								onSpeak={speak}
							/>
						) : (
							<VocaAnalysis
								currentVocabulary={currentVocabulary}
								currentIndex={currentIndex}
								totalCount={vocabularyList.length}
								onPrevious={handlePrevious}
								onNext={handleNext}
								onWritingStart={handleWritingStart}
								onSpeak={speak}
							/>
						)}
					</div>
				</div>

				<DialogFooter className="w-full pt-6">
					<Button 
						variant="default" 
						size="xl"
						onClick={onClose} 
						className="w-full text-[18px] h-[52px]"
					>
						{isWritingMode ? '연습 종료하기' : '그만볼게요'}
					</Button>
				</DialogFooter>
			</DialogContent>
		</Dialog>
	);
}
