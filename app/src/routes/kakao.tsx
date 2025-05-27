import { createFileRoute, useNavigate, useSearch } from '@tanstack/react-router'
import { useEffect, useState, useRef } from 'react'
import { Spinner } from '@/shared/ui'
import { useToken } from '@/shared/hooks/use-token'
import { axiosClient } from '@/shared/api/axios'
import { storage } from '@/shared/utils/storage'

interface KakaoSearch {
  code?: string
}

interface AuthResponse {
  registered: boolean
  clientId: string
  nickname: string
  userType: 'STUDENT' | 'TEACHER' | 'UNREGISTERED'
  accessToken?: string
  refreshToken?: string
}

export const Route = createFileRoute('/kakao')({
  component: KakaoCallback,
  validateSearch: (search: Record<string, unknown>): KakaoSearch => {
    return {
      code: search.code as string | undefined,
    }
  },
})

function KakaoCallback() {
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()
  const { code } = useSearch({ from: '/kakao' })
  const { setTokens } = useToken()
  const isProcessed = useRef(false)

  useEffect(() => {
    const handleCallback = async () => {
      if (isProcessed.current) return
      isProcessed.current = true

      try {
        if (!code) {
          throw new Error('인증 코드가 없습니다.')
        }

        const data = await axiosClient.get('/kakao/callback', {
          params: {
            code,
          },
        }) as unknown as AuthResponse

        if (data.registered) {
          // 이미 가입된 사용자
          if (data.accessToken && data.refreshToken) {
            setTokens(data.accessToken, data.refreshToken)
          }
          navigate({ to: '/' })
        } else {
          // 미가입 사용자
          storage.setClientId(data.clientId)
          navigate({ 
            to: '/signup', 
            search: { 
              nickname: data.nickname,
              userType: data.userType
            } 
          })
        }
      } catch (err) {
        console.error('카카오 로그인 처리 중 오류:', err)
        setError(err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.')
      } finally {
        setIsLoading(false)
      }
    }

    handleCallback()
  }, [code, navigate, setTokens])

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-center">
          <div className="w-full flex justify-center">
            <Spinner className="mb-4 h-12 w-12" />          
          </div>
          <h1 className="mb-2 text-2xl font-bold">카카오 로그인 처리 중</h1>
          <p className="text-gray-600">잠시만 기다려주세요...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-center">
          <h1 className="mb-4 text-2xl font-bold text-red-600">오류 발생</h1>
          <p className="text-gray-600">{error}</p>
          <button
            onClick={() => navigate({ to: '/' })}
            className="mt-4 rounded-md bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
            type="button"
          >
            홈으로 돌아가기
          </button>
        </div>
      </div>
    )
  }

  return null
}
