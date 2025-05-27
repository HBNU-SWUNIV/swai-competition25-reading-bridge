import { useState } from 'react';
import { Volume2, Square } from 'lucide-react';
import { Button } from './button';
import { cn } from '@/lib/utils';

interface SoundButtonProps {
  onSpeak: (text: string) => void;
  text: string;
  size?: 'default' | 'sm' | 'lg' | 'icon';
  variant?: 'default' | 'ghost' | 'outline';
  className?: string;
  isPlaying?: boolean;
  onPlayStateChange?: (isPlaying: boolean) => void;
}

export function SoundButton({
  onSpeak,
  text,
  size = 'icon',
  variant = 'ghost',
  className,
  isPlaying: externalIsPlaying,
  onPlayStateChange,
}: SoundButtonProps) {
  const [internalIsPlaying, setInternalIsPlaying] = useState(false);
  const isPlaying = externalIsPlaying ?? internalIsPlaying;

  const handleClick = () => {
    if (isPlaying) {
      setInternalIsPlaying(false);
      onPlayStateChange?.(false);
    } else {
      setInternalIsPlaying(true);
      onPlayStateChange?.(true);
      onSpeak(text);
      setTimeout(() => {
        setInternalIsPlaying(false);
        onPlayStateChange?.(false);
      }, 2000);
    }
  };

  return (
    <Button
      variant={variant}
      size={size}
      onClick={handleClick}
      className={cn(
        'rounded-full hover:bg-blue-50 hover:text-blue-600',
        isPlaying && 'bg-blue-50 text-blue-600',
        'rounded-full border border-slate-200',
        className
      )}
    >
      {isPlaying ? (
        <Square className="h-4 w-4" />
      ) : (
        <Volume2 className="h-4 w-4" />
      )}
    </Button>
  );
} 