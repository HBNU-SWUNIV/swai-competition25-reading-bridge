import { useQuery, useQueryClient } from '@tanstack/react-query'
import { axiosClient } from '@/shared/api/axios'
import { useToken } from './use-token'
import { toast } from 'sonner'
import { useState } from 'react';

interface UserInfo {
  name: string;
  id: number;
  clientId: string;
  userType: 'STUDENT' | 'TEACHER' | 'UNREGISTERED';
}

interface AuthHookStates {
  isAuthenticated: boolean;
  my: UserInfo | undefined;
  logout: () => void;
}

const fetchMyInfo = async (): Promise<UserInfo> => axiosClient.get('/users/me') as unknown as UserInfo;

export const useAuth = (): AuthHookStates => {
  const { getAccessToken, hasTokens, removeTokens } = useToken()
  const [token, setToken] = useState(() => getAccessToken());
  const queryClient = useQueryClient()

  const { data: my } = useQuery({
    queryKey: ['my'],
    queryFn: fetchMyInfo,
    enabled: !!token,
  });

  const logout = () => {
    removeTokens();
    setToken(null);
    queryClient.invalidateQueries({ queryKey: ['my'] });
    toast.success('로그아웃되었습니다.');
  }

  return {
    isAuthenticated: hasTokens(),
    my,
    logout,
  };
}
