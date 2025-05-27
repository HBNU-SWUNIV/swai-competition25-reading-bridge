import type { WritingStep } from '@/shared/api/types';
import { Badge } from '@/shared/ui/badge';
import { Check } from 'lucide-react';

interface WritingStepsProps {
	steps: WritingStep[];
}

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

export function WritingSteps({ steps }: WritingStepsProps) {
	return (
		<div className="space-y-6">
			<ul className="space-y-4">
				{steps.map((step) => (
					<li 
						key={`${step.syllable}-${step.phoneme}-${step.step}`}
						className="flex items-center gap-4 border-b pb-4 last:border-b-0"
					>
						<div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-primary text-primary-foreground text-lg font-bold">
							{step.step}
						</div>
						<div className="flex-1 space-y-2">
							<div className="flex items-center gap-2">
								<h4 className="text-xl font-bold">{step.phoneme}</h4>
								<span className="text-sm text-muted-foreground">
									({step.syllable} 음절)
								</span>
							</div>
							<div className="flex items-center gap-2">
								<Badge variant="white" className="text-xs border border-gray-200">
									{step.phoneme === 'ㅏ' || step.phoneme === 'ㅜ' || step.phoneme === 'ㅕ' ? '모음' : '자음'}
								</Badge>
								<span className="text-sm text-muted-foreground">
									{step.phoneme === 'ㅏ' || step.phoneme === 'ㅜ' || step.phoneme === 'ㅕ' 
										? '세로선 먼저, 가로선 나중에' 
										: '획순을 지켜서 써주세요'}
								</span>
							</div>
						</div>
					</li>
				))}
			</ul>
		</div>
	);
} 