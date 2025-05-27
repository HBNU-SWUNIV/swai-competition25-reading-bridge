import { createFileRoute, useSearch } from '@tanstack/react-router'
import { SignUpForm } from '@/features/auth/components/signup-form'

interface SignUpSearch {
  nickname?: string
}

export const Route = createFileRoute('/signup/teacher')({
  component: TeacherSignUpPage,
  validateSearch: (search: Record<string, unknown>): SignUpSearch => {
    return {
      nickname: search.nickname as string | undefined,
    }
  },
})

function TeacherSignUpPage() {
  const { nickname } = useSearch({ from: '/signup/teacher' })

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="container mx-auto px-4">
        <div className="max-w-md mx-auto">
          <SignUpForm
            userType="TEACHER"
            defaultNickname={nickname}
          />
        </div>
      </div>
    </div>
  )
} 