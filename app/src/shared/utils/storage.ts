const STORAGE_KEYS = {
  SIGNUP_CLIENT_ID: 'signup_client_id',
} as const

export const storage = {
  getClientId: () => {
    return localStorage.getItem(STORAGE_KEYS.SIGNUP_CLIENT_ID)
  },
  setClientId: (clientId: string) => {
    localStorage.setItem(STORAGE_KEYS.SIGNUP_CLIENT_ID, clientId)
  },
  removeClientId: () => {
    localStorage.removeItem(STORAGE_KEYS.SIGNUP_CLIENT_ID)
  },
} 