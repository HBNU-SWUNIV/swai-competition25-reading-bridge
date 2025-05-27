import { useCallback, useRef, useState } from 'react';
import { fetchSpeechAudio } from '@/features/viewer/api/speech-api';
import { toast } from 'sonner';

interface UseTextToSpeechOptions {
  onStart?: () => void;
  onEnd?: () => void;
  onError?: (error: Error) => void;
}

export function useTextToSpeech(options: UseTextToSpeechOptions = {}) {
  const { onStart, onEnd, onError } = options;
  const [isPlaying, setIsPlaying] = useState(false);
  const [activeBlockId, setActiveBlockId] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const controllerRef = useRef<AbortController | null>(null);

  const cancelCurrentAudio = useCallback(() => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
    }
    if (controllerRef.current) {
      controllerRef.current.abort();
      controllerRef.current = null;
    }
    setIsPlaying(false);
    setActiveBlockId(null);
  }, []);

  const speak = useCallback(async (text: string, blockId?: string) => {
    if (loading) return;
    try {
      cancelCurrentAudio();

      controllerRef.current = new AbortController();
      
      setLoading(true);
      setIsPlaying(true);
      if (blockId) setActiveBlockId(blockId);
      onStart?.();

      const toastId = toast.loading('음성을 준비하고 있습니다...');

      const audioBlob = await fetchSpeechAudio({ 
        text, 
        voice: 'fable', 
        model: 'gpt-4o-mini-tts' 
      });

      const audioUrl = URL.createObjectURL(audioBlob);
      const audio = new Audio(audioUrl);
      audioRef.current = audio;

      audio.onended = () => {
        URL.revokeObjectURL(audioUrl);
        setIsPlaying(false);
        setActiveBlockId(null);
        onEnd?.();
        audioRef.current = null;
      };

      audio.onerror = () => {
        URL.revokeObjectURL(audioUrl);
        setIsPlaying(false);
        setActiveBlockId(null);
        onError?.(new Error('오디오 재생 중 오류가 발생했습니다.'));
        audioRef.current = null;
      };

      audio.onplay = () => {
        toast.dismiss(toastId);
      };

      await audio.play();
    } catch (error) {
      if ((error as Error).name === 'AbortError') {
        return;
      }
      setIsPlaying(false);
      setActiveBlockId(null);
      onError?.(error as Error);
      toast.error('음성 재생 중 오류가 발생했습니다.');
      console.error('음성 재생 중 오류:', error);
    } finally {
      setLoading(false);
    }
  }, [cancelCurrentAudio, onStart, onEnd, onError, loading]);

  return { speak, isPlaying, activeBlockId, loading, cancelCurrentAudio };
} 