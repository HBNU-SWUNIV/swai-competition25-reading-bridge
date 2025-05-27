import { Button } from '@/shared/ui/button';
import { Typography } from '@/shared/ui/typography';
import { useLocation, useRouter } from '@tanstack/react-router';
import { useAuth } from '@/shared/hooks/use-auth';
import { X, Menu } from 'lucide-react';
import type { ReactNode } from 'react';

interface SidebarProps {
	open: boolean;
	onClose: () => void;
	onOpen: () => void;
	children?: ReactNode;
}

export function Sidebar({ open, onClose, onOpen }: SidebarProps) {
	const { my, logout } = useAuth();
	const router = useRouter();
	const location = useLocation({
		select: (location) => location.pathname,
	});

	const onClickLogout = () => {
		logout();
		router.navigate({ to: '/' });
	};

	const onToggleSidebar = () => {
		if (open) {
			onClose();
		} else {
			onOpen();
		}
	}

	const isActive = (path: string): boolean => location.includes(path);

	const getNavButtonClass = (isActivePath: boolean): string => {
		const baseClass = 'w-full justify-start text-lg h-12';
		const activeClass = 'bg-blue-50 text-blue-600 hover:bg-blue-100';
		const inactiveClass = 'text-gray-600 hover:bg-gray-50 hover:text-blue-600';

		return `${baseClass} ${isActivePath ? activeClass : inactiveClass}`;
	};

	return (
		<>
			{/* 오버레이: 모바일에서만 */}
			{open && (
				<div
					className="fixed inset-0 z-30 bg-black/30 md:hidden"
					onClick={onToggleSidebar}
					onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') onToggleSidebar() }}
					tabIndex={0}
					role="button"
					aria-label="사이드바 오버레이 닫기"
				/>
			)}
<aside
  className={`fixed md:sticky top-0 h-screen z-40 left-0 flex flex-col justify-between bg-white border-r py-8 transition-all duration-200
    ${open ? 'w-80 md:static md:translate-x-0' : 'w-12 md:w-12'}
    ${open ? 'translate-x-0' : '-translate-x-0'}
  `}
>
				{/* 닫힌 상태: 햄버거 버튼만 */}
				{!open && (
					<button
						className="flex items-center justify-center w-12 h-12 text-gray-600 hover:text-blue-600 focus:outline-none"
						onClick={onOpen}
						aria-label="사이드바 열기"
						type="button"
					>
						<Menu className="w-6 h-6" />
					</button>
				)}
				{/* 열린 상태: 전체 메뉴 */}
				{open && (
					<>
						<div className="h-full">
							<div className="flex items-center px-6 py-4 justify-between">
								<div className="flex items-center w-full justify-between">
									<div className="flex">
									<div className="mr-2 h-6 w-6 rounded bg-dyslexia-blue" />
									<Typography variant="h3" size="lg" weight="bold" color="primary">
										리딩브릿지
									</Typography>
									</div>


									<button
										className="ml-2 text-gray-500 hover:text-gray-800"
										onClick={onClose}
										onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') onClose() }}
										aria-label="사이드바 닫기"
										type="button"
									>
										<X className="w-6 h-6" />
									</button>
								</div>
								<button
									className="md:hidden p-2 ml-2 text-gray-500 hover:text-gray-800"
									onClick={onClose}
									onKeyDown={e => { if (e.key === 'Enter' || e.key === ' ') onClose() }}
									aria-label="사이드바 닫기"
									type="button"
								>
									<X className="w-6 h-6" />
								</button>
							</div>

							<nav className="mt-8 flex flex-col space-y-2 px-8">
								<Button
									variant="ghost"
									className={getNavButtonClass(isActive('/teacher/dashboard'))}
									onClick={() => router.navigate({ to: '/teacher/dashboard' })}
								>
									대시보드
								</Button>
								<Button
									variant="ghost"
									className={getNavButtonClass(isActive('/teacher/documents'))}
									onClick={() => router.navigate({ to: '/teacher/documents' })}
								>
									콘텐츠 관리
								</Button>
								<Button
									variant="ghost"
									className={getNavButtonClass(isActive('/teacher/student'))}
									onClick={() => router.navigate({ to: '/teacher/student' })}
								>
									학생 관리
								</Button>
							</nav>
						</div>

						<div className="mt-6 border-t px-6 pt-4">
							<div className="flex items-center space-x-2">
								{/* <div className="h-10 w-10 rounded-full bg-gray-200" /> */}
								<div>
									<Typography variant="p" weight="semibold" className="text-gray-900">
										{my?.name} 선생님
									</Typography>
								</div>
							</div>
							<Button
								variant="outline"
								className="mt-4 w-full justify-center text-gray-600 hover:bg-gray-50"
								onClick={onClickLogout}
							>
								로그아웃
							</Button>
						</div>
					</>
				)}
			</aside>
		</>
	);
}
