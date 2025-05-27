import { Button } from '@/shared/ui'
import { useNavigate } from '@tanstack/react-router'

const SignUpPage = () => {
  const navigate = useNavigate()

  return (
    <div className="container mx-auto max-w-md py-8">
      <h1 className="text-2xl font-bold text-center mb-8">회원가입</h1>
      <div className="space-y-4">
        <Button
          className="w-full"
          onClick={() => navigate({ to: '/signup/teacher' })}
        >
          교사로 회원가입
        </Button>
        <Button
          className="w-full"
          onClick={() => navigate({ to: '/signup/student' })}
        >
          학생으로 회원가입
        </Button>
      </div>
    </div>
  )
}

export default SignUpPage
