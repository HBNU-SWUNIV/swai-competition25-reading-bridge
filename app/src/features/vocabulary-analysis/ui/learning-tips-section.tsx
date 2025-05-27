import type { LearningTips } from '@/shared/api/types';
import { Card, CardContent } from '@/shared/ui/card';
import { AlertCircle, BookOpen, Repeat } from 'lucide-react';
import { Badge } from '@/shared/ui/badge';

interface LearningTipsSectionProps {
	tips: LearningTips;
}

export function LearningTipsSection({ tips }: LearningTipsSectionProps) {
	return (
		<div className="space-y-6">
			{/* 자주하는 실수 */}
			<Card>
				<CardContent className="flex items-start gap-4 p-6">
					<div className="flex h-10 w-10 items-center justify-center rounded-full bg-red-100">
						<AlertCircle className="h-6 w-6 text-red-600" />
					</div>
					<div className="flex-1">
						<h4 className="mb-3 text-lg font-bold">자주하는 실수</h4>
						<ul className="list-inside list-disc space-y-2">
							{tips.commonMistakes.map((mistake, index) => (
								<li 
									key={`mistake-${index}`} 
									className="text-sm text-muted-foreground"
								>
									{mistake}
								</li>
							))}
						</ul>
					</div>
				</CardContent>
			</Card>

			{/* 연습할 단어 */}
			<Card>
				<CardContent className="flex items-start gap-4 p-6">
					<div className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-100">
						<BookOpen className="h-6 w-6 text-blue-600" />
					</div>
					<div className="flex-1">
						<h4 className="mb-3 text-lg font-bold">연습할 단어</h4>
						<div className="flex flex-wrap gap-2">
							{tips.practiceWords.map((word) => (
								<Badge 
									key={word} 
									variant="blue"
									className="text-sm"
								>
									{word}
								</Badge>
							))}
						</div>
					</div>
				</CardContent>
			</Card>

			{/* 비슷한 발음의 단어 */}
			<Card>
				<CardContent className="flex items-start gap-4 p-6">
					<div className="flex h-10 w-10 items-center justify-center rounded-full bg-purple-100">
						<Repeat className="h-6 w-6 text-purple-600" />
					</div>
					<div className="flex-1">
						<h4 className="mb-3 text-lg font-bold">비슷한 발음의 단어</h4>
						<div className="flex flex-wrap gap-2">
							{tips.rhymingWords.map((word) => (
								<Badge 
									key={word} 
									variant="white"
									className="border border-gray-200 text-sm"
								>
									{word}
								</Badge>
							))}
						</div>
					</div>
				</CardContent>
			</Card>
		</div>
	);
} 