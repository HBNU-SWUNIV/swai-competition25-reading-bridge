import { SignUpForm } from '@/features/auth/components/signup-form'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/signup/teacher')({
  component: TeacherSignUpPage,
})

function TeacherSignUpPage() {
  return (
    <div className="container mx-auto max-w-md py-8">
      <h1 className="text-2xl font-bold text-center mb-8">교사 회원가입</h1>
      <SignUpForm userType="TEACHER" />
    </div>
  )
} 