import { createFileRoute, useSearch } from '@tanstack/react-router'
import { SignUpForm } from '@/features/auth/components/signup-form'

interface SignUpSearch {
  nickname?: string
}

export const Route = createFileRoute('/signup/student')({
  component: StudentSignUpPage,
  validateSearch: (search: Record<string, unknown>): SignUpSearch => {
    return {
      nickname: search.nickname as string | undefined,
    }
  },
})

function StudentSignUpPage() {
  const { nickname } = useSearch({ from: '/signup/student' })

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="container mx-auto px-4">
        <div className="max-w-md mx-auto">
          <SignUpForm
            userType="STUDENT"
            defaultNickname={nickname}
          />
        </div>
      </div>
    </div>
  )
} 