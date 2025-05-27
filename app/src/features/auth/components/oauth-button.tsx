import { Button } from '@/shared/ui/button'
import { OAUTH_PROVIDERS, type OAuthProvider } from '@/shared/constants/auth'

interface OAuthButtonProps {
  provider: OAuthProvider
  className?: string
}

export const OAuthButton = ({ provider, className }: OAuthButtonProps) => {
  const { url, label } = OAUTH_PROVIDERS[provider]

  const handleClick = () => {
    window.location.href = url
  }

  const getButtonStyle = (provider: OAuthProvider) => {
    switch (provider) {
      case 'KAKAO':
        return 'bg-[#FEE500] text-[#000000] hover:bg-[#FEE500]/90 font-bold'
      default:
        return ''
    }
  }

  return (
    <Button
      type="button"
      onClick={handleClick}
      className={`${getButtonStyle(provider)} ${className ?? ''}`}
      size="lg"
    >
      {provider === 'KAKAO' && (
        <img
          src="/kakao-logo.png"
          alt="카카오"
          className="w-5 h-5 mr-2"
        />
      )}
      {label}
    </Button>
  )
} 