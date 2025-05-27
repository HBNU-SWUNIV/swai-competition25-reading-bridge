import { Typography } from "@/shared/ui/typography";
import { Button } from "@/shared/ui/button";
import { useAuth } from "@/shared/hooks/use-auth";

export function TopHeader() {
    const { my } = useAuth();
    return (
        <header className="flex flex-col border-b bg-white px-4 py-3 md:flex-row md:items-center md:justify-between md:py-2">
            <div>
                <Typography variant="h2" size="xl" weight="semibold" className="text-gray-800">
                    교사용 대시보드
                </Typography>
                <Typography variant="p" size="sm" className="text-gray-500">
                    학생들의 학습 현황을 관리하고 맞춤형 컨텐츠를 생성하세요.
                </Typography>
            </div>

            <div className="mt-3 flex items-center space-x-4 md:mt-0">

                <Button variant="ghost" className="relative p-2 text-gray-600">
                    {/* <div className="h-5 w-5 bg-current" /> */}
                    {/* <span className="absolute right-1 top-1 block h-2 w-2 rounded-full bg-red-500 ring-2 ring-white" /> */}
                </Button>

                <div className="flex items-center space-x-1">
                    {/* <div className="h-6 w-6 rounded-full bg-gray-200" /> */}
                    <Typography variant="p" size="sm" className="text-gray-700">
                        {my?.name}
                    </Typography>
                    {/* <div className="h-4 w-4 bg-current text-gray-500" /> */}
                </div>
            </div>
        </header>
    );
}
