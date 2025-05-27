export const OAUTH_BASE_URL = 'http://localhost:8080/api/oauth2'

const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_CLIENT_ID
const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI

export const KAKAO_AUTH_URL = `https://kauth.kakao.com/oauth/authorize?client_id=${KAKAO_CLIENT_ID}&redirect_uri=${KAKAO_REDIRECT_URI}&response_type=code`

export const OAUTH_PROVIDERS = {
  KAKAO: {
    name: 'kakao',
    url: KAKAO_AUTH_URL,
    label: '카카오로 시작하기',
  },
} as const

export type OAuthProvider = keyof typeof OAUTH_PROVIDERS
