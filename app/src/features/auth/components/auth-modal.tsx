import { Button } from "@/shared/ui/button"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/shared/ui/dialog"
import { OAuthButton } from "./oauth-button"

interface AuthModalProps {
  isOpen: boolean
  onClose: () => void
}

export const AuthModal = ({ isOpen, onClose }: AuthModalProps) => {
  const handleKakaoLogin = () => {
    // TODO: 카카오 로그인 구현
    console.log("카카오 로그인")
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>로그인 / 회원가입</DialogTitle>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          <OAuthButton provider="KAKAO" />
        </div>
      </DialogContent>
    </Dialog>
  )
}
