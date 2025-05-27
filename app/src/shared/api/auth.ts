import type { SignUpRequestDto, SignUpResponseDto } from '../types/auth'
import { axiosClient } from './axios'

export const signUp = async (data: SignUpRequestDto): Promise<SignUpResponseDto> => {
  return axiosClient.post('/users/signup', data)
} 