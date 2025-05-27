export type UserType = 'STUDENT' | 'TEACHER' | 'UNREGISTERED'

export type Grade = 'GRADE_1' | 'GRADE_2' | 'GRADE_3' | 'GRADE_4' | 'GRADE_5' | 'GRADE_6'

export interface SignUpRequestDto {
  clientId: string
  name: string
  userType: UserType
  grade?: Grade
  type: string
  interestIds: number[]
  organization: string
}

export interface SignUpResponseDto {
  id: number
  name: string
  userType: UserType
  accessToken: string
  refreshToken: string
}

export type SignUpFormData = {
  name: string
  grade?: Grade
  organization: string
}