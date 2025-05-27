import { SignUpForm } from '@/features/auth/components/signup-form'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/signup/student')({
  component: StudentSignUpPage,
})

function StudentSignUpPage() {
  return (
    <div className="container mx-auto max-w-md py-8">
      <h1 className="text-2xl font-bold text-center mb-8">학생 회원가입</h1>
      <SignUpForm userType="STUDENT" />
    </div>
  )
} 