import { useState, useRef } from 'react';
import { useLongPress } from '@/shared/hooks/use-long-press';
import { useTextToSpeech } from '@/shared/hooks/use-text-to-speech';
import { VocabularyModal } from './vocabulary-modal';
import { useVocabularyAnalysis } from '../model/use-vocabulary-analysis';
import { Button } from '@/shared/ui/button';
import { Volume, Loader2 } from 'lucide-react';
import { cn } from '@/shared/lib/utils';
import { toast } from 'sonner';

interface LongPressTextProps {
	text: string;
	documentId: number;
	pageNumber: number;
	blockId: string;
	className?: string;
}

export function LongPressText({
	text,
	documentId,
	pageNumber,
	blockId,
	className,
}: LongPressTextProps) {
	const [isModalOpen, setIsModalOpen] = useState(false);
	const [isPressed, setIsPressed] = useState(false);
	const { data, searchVocabulary } = useVocabularyAnalysis();
	const { speak, isPlaying, loading, activeBlockId, cancelCurrentAudio } =
		useTextToSpeech({
			onStart: () => setIsPressed(true),
			onEnd: () => setIsPressed(false),
			onError: () => setIsPressed(false),
		});
	const hasShownToastRef = useRef(false);
	const textRef = useRef<HTMLSpanElement>(null);

	const handleLongPress = () => {
		setIsModalOpen(true);
		searchVocabulary({
			documentId,
			pageNumber,
			blockId,
		}).catch(console.error);
	};

	const showToastAtElement = () => {
		if (hasShownToastRef.current || !textRef.current) return;

		const rect = textRef.current.getBoundingClientRect();
		const bottomY = rect.bottom;
		
		toast("계속 누르면 어휘 학습을 할 수 있어요!", {
			className: "text-[16px] py-3 px-4",
			position: "top-center",
			duration: 500,
			style: {
				top: `${bottomY - 26}px`,
			},
		});
		hasShownToastRef.current = true;
	};

	const longPressEvent = useLongPress({
		onLongPress: handleLongPress,
		onStart: () => {
			setIsPressed(true);
			showToastAtElement();
		},
		onFinish: () => setIsPressed(false),
		onCancel: () => setIsPressed(false),
		threshold: 500,
	});

	const handleClick = () => {
		if (isPlaying && activeBlockId === blockId) {
			cancelCurrentAudio();
		} else {
			speak(text, blockId);
		}
	};

	const isActiveBlock = activeBlockId === blockId;

	return (
		<div className="group relative inline-block">
			<span
				ref={textRef}
				{...longPressEvent}
				onClick={handleClick}
				className={cn(
					'cursor-pointer transition-colors duration-300',
					isPressed && 'bg-blue-50',
					isActiveBlock && 'bg-yellow-100',
					className,
				)}
				style={{
					cursor: 'pointer',
					userSelect: 'none',
					WebkitUserSelect: 'none',
				}}
			>
				{text}
			</span>
			<Button
				variant="ghost"
				size="icon"
				className="border border-slate-200 absolute -right-8 top-1/2 -translate-y-1/2 opacity-0 transition-opacity group-hover:opacity-100"
				onClick={handleClick}
				disabled={loading && !isActiveBlock}
			>
				{loading && isActiveBlock ? (
					<Loader2 className="h-4 w-4 animate-spin" />
				) : (
					<Volume className="h-4 w-4" />
				)}
			</Button>
			<VocabularyModal
				isOpen={isModalOpen}
				onClose={() => setIsModalOpen(false)}
				vocabularyList={
					data?.map((item) => ({
						...item,
						originalSentence: text,
					})) ?? []
				}
			/>
		</div>
	);
}
