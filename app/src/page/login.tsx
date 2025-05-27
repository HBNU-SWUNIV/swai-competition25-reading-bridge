import { LoginForm } from '@/features/auth/components/login-form'

const LoginPage = () => {
  return (
    <div className="container mx-auto max-w-md py-8">
      <h1 className="text-2xl font-bold text-center mb-8">로그인</h1>
      <LoginForm />
    </div>
  )
}

export default LoginPage 