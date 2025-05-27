import { useState, useRef } from 'react'
import { fetchSpeechAudio } from '../api/speech-api'
import { toast } from 'sonner'

export function useBlockSpeech() {
  const [activeBlockId, setActiveBlockId] = useState<string | null>(null)
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const [loading, setLoading] = useState(false);
  
  const speak = async (blockId: string, text: string) => {
    if (loading) return;
    if (audioRef.current) {
      audioRef.current.onended = null
      audioRef.current.onerror = null
      audioRef.current.pause()
      audioRef.current = null
    }
    setActiveBlockId(blockId)
    const toastId = toast.loading('오디오를 불러오는 중...')
    try {
      setLoading(true)
      const blob = await fetchSpeechAudio({ text, voice: 'fable', model: 'gpt-4o-mini-tts' })
      toast.dismiss(toastId)
      const url = URL.createObjectURL(blob)
      const audio = new Audio(url)
      audioRef.current = audio
      audio.onended = () => {
        setActiveBlockId(null)
        URL.revokeObjectURL(url)
      }
      audio.onerror = () => {
        setActiveBlockId(null)
        URL.revokeObjectURL(url)
      }
      audio.play()
    } catch (e) {
      toast.dismiss(toastId)
      setActiveBlockId(null)
      toast.error('오디오를 불러오지 못했습니다')
    } finally {
      setLoading(false)
    }
  }

  const stop = () => {
    if (audioRef.current) {
      audioRef.current.onended = null
      audioRef.current.onerror = null
      audioRef.current.pause()
      audioRef.current = null
    }
    setActiveBlockId(null)
  }

  return { activeBlockId, speak, stop }
} 