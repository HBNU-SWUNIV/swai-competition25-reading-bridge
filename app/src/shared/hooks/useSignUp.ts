import { useMutation } from '@tanstack/react-query'
import { signUp } from '../api/auth'
import type { SignUpRequestDto, SignUpResponseDto } from '../types/auth'
import type { AxiosError } from 'axios'

export const useSignUp = () => {
  return useMutation<SignUpResponseDto, AxiosError, SignUpRequestDto>({
    mutationFn: signUp,
    onSuccess: (data) => {
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
    },
    onError: (error) => {
      console.error('회원가입 실패:', error.message)
    },
  })
} 