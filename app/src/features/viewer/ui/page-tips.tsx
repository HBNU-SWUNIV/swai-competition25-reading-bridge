import type { PageTipResponse } from '../model/types';
import { fetchSpeechAudio } from '../api/speech-api';
import { Typography } from '@/shared/ui';
import { Volume2, Info } from 'lucide-react';
import { toast } from 'sonner';
import type React from 'react';
import { useState, useRef } from 'react';

interface PageTipsProps {
	tips: PageTipResponse[];
	fontSize: number;
}

export const PageTips: React.FC<PageTipsProps> = ({ tips, fontSize }) => {
	const [loadingId, setLoadingId] = useState<string | null>(null);
	const audioRef = useRef<HTMLAudioElement | null>(null);

	if (!tips || tips.length === 0) return null;

	const handleSpeak = async (id: string, text: string) => {
		if (loadingId) return;
		if (audioRef.current) {
			audioRef.current.pause();
			audioRef.current = null;
		}
		setLoadingId(id);
		const toastId = toast.loading('오디오를 불러오는 중...');
		try {
			const blob = await fetchSpeechAudio({ text, voice: 'echo', model: 'tts-1' });
			toast.dismiss(toastId);
			const url = URL.createObjectURL(blob);
			const audio = new Audio(url);
			audioRef.current = audio;
			audio.onended = () => {
				setLoadingId(null);
				URL.revokeObjectURL(url);
			};
			audio.onerror = () => {
				setLoadingId(null);
				URL.revokeObjectURL(url);
			};
			audio.play();
		} catch (e) {
			toast.dismiss(toastId);
			setLoadingId(null);
			toast.error('오디오를 불러오지 못했습니다');
		}
	};

	return (
		<div className="mt-6 bg-blue-50 p-4 rounded-md">
			<div className="flex items-center mb-2">
				<Info className="w-5 h-5 text-blue-500 mr-2" />
				<Typography variant="h4" className="text-blue-700">
					학습 팁
				</Typography>
			</div>
			<div className="space-y-3">
				{tips.map((tip) => (
					<div key={tip.id} className="bg-white p-3 rounded shadow-sm">
						<div className="flex justify-between">
							<Typography variant="p" className="font-bold text-blue-800">
								{tip.term}
							</Typography>
							<button
								type="button"
								onClick={() => handleSpeak(tip.id.toString(), tip.readAloudText || tip.simplifiedExplanation)}
								className="text-blue-600 hover:text-blue-800"
								disabled={loadingId === tip.id.toString()}
							>
								<Volume2 className="w-4 h-4" />
							</button>
						</div>
						<Typography
							variant="p"
							className="text-gray-700 mt-1"
							style={{ fontSize: `${fontSize - 1}px` }}
						>
							{tip.simplifiedExplanation}
						</Typography>
					</div>
				))}
			</div>
		</div>
	);
};
