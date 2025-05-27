import { useNavigate } from '@tanstack/react-router'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/shared/ui/dialog'
import { Button } from '@/shared/ui/button'
import type { UserType } from '@/shared/types/auth'

interface SuccessModalProps {
  isOpen: boolean
  onOpenChange: (open: boolean) => void
  userType: UserType
}

export const SuccessModal = ({ isOpen, onOpenChange, userType }: SuccessModalProps) => {
  const navigate = useNavigate()

  const handleConfirm = () => {
    onOpenChange(false)
    navigate({ to: '/' })
  }

  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold text-center">
            회원가입을 축하합니다! 🎉
          </DialogTitle>
          <DialogDescription className="text-center text-base mt-4">
            {userType === 'STUDENT' 
              ? '이제 난독증 친화적인 교육 자료를 이용하실 수 있습니다.'
              : '이제 학생들을 위한 교육 자료를 관리하실 수 있습니다.'}
          </DialogDescription>
        </DialogHeader>
        <div className="flex justify-center mt-6">
          <Button 
            onClick={handleConfirm}
            className="w-full h-12 text-base font-semibold"
          >
            시작하기
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
} 