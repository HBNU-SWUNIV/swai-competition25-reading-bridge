import { axiosClient } from '@/shared/api/axios';

export async function fetchSpeechAudio({ text, voice, model }: { text: string, voice?: string, model?: string }) {
  const data = await axiosClient.post('/openai/speech', { text, voice, model: model || 'tts-1' }, { responseType: 'blob' })
  return data as unknown as Blob;
}
