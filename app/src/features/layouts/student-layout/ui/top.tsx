import { useState } from 'react';
import { useNavigate } from '@tanstack/react-router';
import { Typography } from '@/shared/ui/typography';
import { useAuth } from '@/shared/hooks/use-auth';
import { Menu, ChevronDown } from 'lucide-react';

interface TopHeaderProps {
  onToggleSidebar: () => void;
}

export function TopHeader({ onToggleSidebar }: TopHeaderProps) {
  const navigate = useNavigate();
  const { my, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="flex h-14 items-center justify-between border-b bg-white px-4">
      <button
        className="mr-2 flex h-8 w-8 items-center justify-center text-gray-600 md:hidden"
        onClick={onToggleSidebar}
        type="button"
        aria-label="사이드바 토글"
      >
        <Menu className="h-6 w-6" />
      </button>

      {/* biome-ignore lint/a11y/useKeyWithClickEvents: <explanation> */}
<div
        className="flex cursor-pointer items-center gap-2"
        onClick={() => navigate({ to: '/student/library' })}
      >
        <Typography variant="p" weight="bold" className="text-lg">
          EduService
        </Typography>
      </div>

      <div className="relative">
        <button
          className="flex items-center gap-1 text-gray-700"
          onClick={() => setMenuOpen((p) => !p)}
          type="button"
        >
          <div className="h-8 w-8 rounded-full bg-gray-200" />
          <Typography variant="p" size="sm">
            {my?.name}
          </Typography>
          <ChevronDown className="h-4 w-4" />
        </button>

        {menuOpen && (
          <ul className="absolute right-0 mt-2 w-40 overflow-hidden rounded-lg border bg-white text-sm shadow-lg">
            <li>
              {/* biome-ignore lint/a11y/useButtonType: <explanation> */}
<button
                className="block w-full px-4 py-2 text-left hover:bg-gray-100"
                onClick={() => {
                  navigate({ to: '/' });
                  setMenuOpen(false);
                }}
              >
                회원정보 수정
              </button>
            </li>
            <li>
              {/* biome-ignore lint/a11y/useButtonType: <explanation> */}
<button
                className="block w-full px-4 py-2 text-left hover:bg-gray-100"
                onClick={() => {
                  logout();
                  navigate({ to: '/' });
                }}
              >
                로그아웃
              </button>
            </li>
          </ul>
        )}
      </div>
    </header>
  );
}
