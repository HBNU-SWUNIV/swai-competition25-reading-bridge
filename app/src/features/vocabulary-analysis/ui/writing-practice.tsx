import { useEffect, useState, useRef } from 'react';
import type { PhonemeAnalysis } from '@/shared/api/types';
import { Button } from '@/shared/ui/button';
import { ProgressBar } from '@/shared/ui/progress-bar';
import { SoundButton } from '@/shared/ui/sound-button';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/ui/card';
import { ArrowLeft, ArrowRight } from 'lucide-react';
import { toast } from 'sonner';
import { useWritingStore } from '../model/writing-store';
import { WritingComponent } from './writing-component';
import { Show, Typography } from '@/shared/ui';

interface WritingPracticeProps {
	word: string;
	phonemeAnalysis: PhonemeAnalysis;
	onBack: () => void;
	onSpeak: (text: string) => void;
}

export function WritingPractice({ word, phonemeAnalysis, onBack, onSpeak }: WritingPracticeProps) {
	const {
		writingData,
		currentSyllableIndex,
		setWritingData,
		setCurrentSyllable,
		reset
	} = useWritingStore();

	const [showResult, setShowResult] = useState(false);

	useEffect(() => {
		reset();
	}, [reset]);

	const syllable = phonemeAnalysis.syllables[currentSyllableIndex];

	console.log({
		currentSyllableIndex,
		syllabelLength: phonemeAnalysis.syllables.length,
	});

	const handleSave = (component: 'initial' | 'medial' | 'final' | 'syllable', imageData: string) => {
		setWritingData(currentSyllableIndex, { [component]: imageData });
		
		const currentData = writingData[currentSyllableIndex] || {};
		const updatedData = { ...currentData, [component]: imageData };
		const requiredComponents: ('initial' | 'medial' | 'final')[] = [];
		
		if (syllable.components.initial?.consonant) {
			requiredComponents.push('initial');
		}
		if (syllable.components.medial?.vowel) {
			requiredComponents.push('medial');
		}
		if (syllable.components.final?.consonant) {
			requiredComponents.push('final');
		}

		// TODO: 이거 나중에 활용해요.  
		const isAllComponentsWritten = requiredComponents.length > 0 && 
			requiredComponents.every(comp => updatedData[comp]);
	};

	const moveToNextSyllable = () => {
		if (currentSyllableIndex < phonemeAnalysis.syllables.length) {
			if (currentSyllableIndex === phonemeAnalysis.syllables.length - 1) {
				// 마지막 음절에서 다음을 누르면 결과 화면 표시
				setShowResult(true);
				toast.success(
					`정말 잘했어요! '${word}' 쓰기를 완성했어요!`,
					{
						duration: 2000,
						position: 'top-center',
					}
				);
			} else {
				setCurrentSyllable(currentSyllableIndex + 1);
			}
		}
	};

	const moveToPreviousSyllable = () => {
		if (currentSyllableIndex > 0) {
			setCurrentSyllable(currentSyllableIndex - 1);
		}
	};

	const progress = ((currentSyllableIndex + 1) / phonemeAnalysis.syllables.length) * 100;

	if (!syllable && !showResult) return null;

	if (showResult) {
		return (
			<div className="flex flex-col h-full">
				<div className="flex items-center justify-between mb-6">
					<h2 className="text-2xl font-bold">쓰기 연습 결과</h2>
				</div>

				<div className="flex-1 overflow-y-auto">
					<div className="grid grid-cols-2 gap-6">
						{Object.entries(writingData).map(([index, data]) => {
							const syllableInfo = phonemeAnalysis.syllables[Number(index)];
							return (
								<Card key={index} className="p-4">
									<CardHeader>
										<CardTitle>{syllableInfo.syllable}</CardTitle>
									</CardHeader>
									<CardContent className="space-y-4">
										<div className="grid grid-cols-3 gap-2">
											{data.initial && (
												<div>
													<Typography variant="p" className="text-sm text-muted-foreground mb-1">초성</Typography>
													<img src={data.initial} alt="초성" className="border rounded" />
												</div>
											)}
											{data.medial && (
												<div>
													<Typography variant="p" className="text-sm text-muted-foreground mb-1">중성</Typography>
													<img src={data.medial} alt="중성" className="border rounded" />
												</div>
											)}
											{data.final && (
												<div>
													<Typography variant="p" className="text-sm text-muted-foreground mb-1">종성</Typography>
													<img src={data.final} alt="종성" className="border rounded" />
												</div>
											)}
										</div>
										{data.syllable && (
											<div>
												<Typography variant="p" className="text-sm text-muted-foreground mb-1">음절</Typography>
												<img src={data.syllable} alt="음절" className="border rounded" />
											</div>
										)}
										{data.word && (
											<div>
												<Typography variant="p" className="text-sm text-muted-foreground mb-1">단어</Typography>
												<img src={data.word} alt="단어" className="border rounded" />
											</div>
										)}
									</CardContent>
								</Card>
							);
						})}
					</div>
				</div>

				<div className="mt-6">
					<Button 
						className="w-full" 
						size="xl" 
						onClick={onBack}
					>
						어휘 분석으로 돌아가기
					</Button>
				</div>
			</div>
		);
	}

	return (
		<div className="flex flex-col h-full">
			{/* 헤더 영역 - 고정 */}
			<div className="flex items-center justify-between mb-6">
				<Button variant="ghost" onClick={onBack}>
					<ArrowLeft className="mr-2 h-4 w-4" />
					돌아가기
				</Button>
				<div className="flex items-center gap-4">
					<h2 className="text-2xl font-bold">{word}</h2>
					<SoundButton text={word} onSpeak={onSpeak} />
				</div>
				<ProgressBar progress={progress} className="w-32" />
			</div>

			{/* 현재 학습 정보 - 고정 */}
			<Card className="mb-6">
				<CardContent className="flex flex-col gap-4 p-6">
					<div className="flex items-center justify-between">
						<div>
							<div className="flex h-[64px] items-center gap-x-2 mb-2">
								<h3 className="text-2xl font-bold">{syllable.syllable}</h3>
								<SoundButton text={syllable.syllable} onSpeak={onSpeak} />
							</div>
							<p className="text-sm text-gray-500">{syllable.writingTips}</p>
						</div>
					</div>
				</CardContent>
			</Card>

			{/* 쓰기 연습 영역 - 스크롤 가능 */}
			<div className="flex-1 overflow-y-auto min-h-0">
				<div className="flex flex-row w-full justify-between pb-6">
					{/* 초성 */}
					{syllable.components.initial && (
						<WritingComponent
							title="초성"
							description={`${syllable.components.initial.consonant} - ${syllable.components.initial.pronunciation}`}
							onSave={(imageData) => handleSave('initial', imageData)}
							initialImage={writingData[currentSyllableIndex]?.initial}
							guideText={syllable.components.initial?.consonant}
						/>
					)}

					{/* 중성 */}
					{syllable.components.medial && (
						<WritingComponent
							title="중성"
							description={`${syllable.components.medial.vowel} - ${syllable.components.medial.pronunciation}`}
							onSave={(imageData) => handleSave('medial', imageData)}
							initialImage={writingData[currentSyllableIndex]?.medial}
							guideText={syllable.components.medial?.vowel}
						/>
					)}

					{/* 종성 */}
					{syllable.components.final && (
						<WritingComponent
							title="종성"
							description={`${syllable.components.final.consonant} - ${syllable.components.final.pronunciation}`}
							onSave={(imageData) => handleSave('final', imageData)}
							initialImage={writingData[currentSyllableIndex]?.final}
							guideText={syllable.components.final?.consonant}
						/>
					)}
				</div>

				{currentSyllableIndex !== phonemeAnalysis.syllables.length && (
        <section className="w-full flex flex-col gap-y-4 items-center">
				<WritingComponent
					title="한번에 써보기"
					description={`${syllable.syllable}를 한 번에 써보세요`}
					width={400}
					height={280}
					onSave={(imageData) => handleSave('syllable', imageData)}
					initialImage={writingData[currentSyllableIndex]?.syllable}
					guideText={syllable.syllable}
				/>
			</section>			
			)}
				{currentSyllableIndex === phonemeAnalysis.syllables.length - 1 && (
					<section className="w-full flex flex-col gap-y-4 items-center pt-8 border-t">
						<WritingComponent
							title="단어 전체 쓰기"
							description={`'${word}'를 한 번에 써보세요`}
							width={600}
							height={280}
							onSave={(imageData) => {
								setWritingData(currentSyllableIndex, { word: imageData });
							}}
							initialImage={writingData[currentSyllableIndex]?.word}
							guideText={word}
							guideTextScale={0.5}
						/>
					</section>
				)}
			</div>

			{/* 네비게이션 버튼 - 고정 */}
			<div className="flex justify-between mt-6">
				<Button
					variant="outline"
					onClick={moveToPreviousSyllable}
					disabled={currentSyllableIndex === 0}
				>
					<ArrowLeft className="mr-2 h-4 w-4" />
					이전 음절
				</Button>
				<Button
					onClick={moveToNextSyllable}
					disabled={currentSyllableIndex === phonemeAnalysis.syllables.length}
				>
					다음 음절
					<ArrowRight className="ml-2 h-4 w-4" />
				</Button>
			</div>
		</div>
	);
} 