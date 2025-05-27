import { Typography } from '@/shared/ui';
import { WritingCanvas } from './writing-canvas';
import { SoundButton } from '@/shared/ui/sound-button';
import { useTextToSpeech } from '@/shared/hooks/use-text-to-speech';
import { toast } from 'sonner';
import { useEffect, useRef } from 'react';

interface WritingComponentProps {
	title: string;
	description: string;
	width?: number;
	height?: number;
	onSave: (imageData: string) => void;
	initialImage?: string;
	guideText?: string;
	guideTextScale?: number;
}

export function WritingComponent({
	title,
	description,
	width = 280,
	height = 280,
	onSave,
	initialImage,
	guideText,
	guideTextScale = 1,
}: WritingComponentProps) {
	const { speak } = useTextToSpeech();

	return (
		<div className="flex flex-col gap-2">
			<div className="flex items-center justify-between">
				<div>
					<Typography variant="h4" className="mb-1">{title}</Typography>
					<Typography variant="p" className="text-muted-foreground">{description}</Typography>
				</div>
				{guideText && (
					<SoundButton text={guideText} onSpeak={speak} />
				)}
			</div>
			<WritingCanvas
				width={width}
				height={height}
				className="bg-white"
				onSave={onSave}
				initialImage={initialImage}
				guideText={guideText}
				guideTextScale={guideTextScale}
			/>
		</div>
	);
} 