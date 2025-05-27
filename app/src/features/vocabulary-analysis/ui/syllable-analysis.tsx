import type { PhonemeComponent, SyllableComponents, SyllableInfo } from '@/shared/api/types';
import { useTextToSpeech } from '@/shared/hooks/use-text-to-speech';
import { Badge } from '@/shared/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/ui/card';
import { SoundButton } from '@/shared/ui/sound-button';

function PhonemeCard({ component, type }: { component: PhonemeComponent | SyllableComponents['medial'] | null; type: 'initial' | 'medial' | 'final' }) {
	const { speak } = useTextToSpeech();

  if (!component) return null;
	return (
		<div className="relative flex flex-col items-center gap-2 rounded-lg border p-3 text-center">
			<div className="text-2xl font-bold">{type === 'medial' ? (component as SyllableComponents['medial']).vowel : (component as PhonemeComponent).consonant}</div>
			<div className="text-sm text-gray-500">{component.pronunciation}</div>
			<div className="text-xs text-gray-400">{component.sound}</div>
			<Badge className={getDifficultyStyle(component.difficulty)}>
				{getDifficultyText(component.difficulty)}
			</Badge>
			<div className="absolute top-2 right-2">
				<SoundButton text={component.sound} onSpeak={() => speak(component.pronunciation)} />
			</div>
		</div>
	);
}

export function SyllableAnalysis({ syllable }: { syllable: SyllableInfo }) {
	const { speak } = useTextToSpeech();
	console.log(syllable);

	return (
		<Card className="mb-4">
			<CardHeader>
				<CardTitle className="flex items-center justify-between">
					<div className="flex items-center gap-2">
						<span className="text-2xl">{syllable.syllable}</span>
						<SoundButton text={syllable.combinedSound} onSpeak={() => speak(syllable.syllable)} />
					</div>
					<span className="text-sm text-gray-500">{syllable.combinedSound}</span>
				</CardTitle>
			</CardHeader>
			<CardContent>
				<div className="grid grid-cols-3 gap-4">
					<PhonemeCard component={syllable.components.initial} type="initial" />
					<PhonemeCard component={syllable.components.medial} type="medial" />
					{syllable.components.final && (
						<PhonemeCard component={syllable.components.final} type="final" />
					)}
				</div>
				<div className="mt-4 text-sm text-gray-600">
					<p>{syllable.writingTips}</p>
				</div>
			</CardContent>
		</Card>
	);
}

const getDifficultyText = (level: string): string | null => {
	switch (level) {
		case 'easy':
			return '쉬움';
		case 'medium':
			return '보통';
		case 'hard':
			return '어려움';
    default:
      return null;
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