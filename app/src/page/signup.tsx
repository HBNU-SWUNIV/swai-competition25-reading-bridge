import { SignUpForm } from "@/features/auth/components/signup-form"

const SignUpPage = () => {
  return (
    <div className="container mx-auto max-w-md py-8">
      <h1 className="text-2xl font-bold text-center mb-8">회원가입</h1>
      <SignUpForm />
    </div>
  )
}

export default SignUpPage 