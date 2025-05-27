import { Button } from "@/shared/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/ui/card"

export const LoginForm = () => {
  const handleKakaoLogin = () => {
    // TODO: 카카오 로그인 구현
    console.log("카카오 로그인")
  }

  return (
    <Card className="w-[400px]">
      <CardHeader>
        <CardTitle className="text-center text-2xl">로그인</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col gap-4">
          <Button
            onClick={handleKakaoLogin}
            className="flex items-center justify-center gap-2 bg-[#FEE500] text-[#000000] hover:bg-[#FEE500]/90"
          >
            <img
              src="/kakao-logo.png"
              alt="카카오 로고"
              width={24}
              height={24}
            />
            카카오로 시작하기
          </Button>
        </div>
      </CardContent>
    </Card>
  )
} 