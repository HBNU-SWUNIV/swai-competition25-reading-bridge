export const textToSpeech = {
  speak: (text: string) => {
    if ('speechSynthesis' in window) {
      const utterance = new SpeechSynthesisUtterance(text)
      utterance.lang = 'ko-KR'
      window.speechSynthesis.speak(utterance)
    }
  },
  stop: () => {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel()
    }
  }
}
