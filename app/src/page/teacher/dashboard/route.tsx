import { Card } from "@/shared/ui/card";
import { Button } from "@/shared/ui/button";
import { Typography } from "@/shared/ui/typography";
import { DataTable, Badge, DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator} from "@/shared/ui";
import { MoreVertical } from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";
import { useRouter } from "@tanstack/react-router";

interface ContentItem {
  id: string;
  title: string;
  subject: string;
  grade: string;
  date: string;
  status: "published" | "draft";
}

const contentData: ContentItem[] = [
  {
    id: "1",
    title: "생태계와 환경",
    subject: "과학",
    grade: "3학년",
    date: "2025-03-10",
    status: "published",
  },
  {
    id: "2",
    title: "우리 동네 탐험",
    subject: "사회",
    grade: "2학년",
    date: "2025-03-09",
    status: "draft",
  },
  {
    id: "3",
    title: "곤충의 한살이",
    subject: "과학",
    grade: "3학년",
    date: "2025-03-08",
    status: "published",
  },
];

const columns: ColumnDef<ContentItem>[] = [
  {
    accessorKey: "title",
    header: "제목",
    size: 100,
  },
  {
    accessorKey: "subject",
    header: "과목",
    size: 80,
    cell: ({ row }) => <div className="text-center">{row.original.subject}</div>,
  },
  {
    accessorKey: "grade",
    header: "학년",
    size: 60,
    cell: ({ row }) => (
      <div className="text-center">{row.original.grade}</div>
    ),
  },
  {
    accessorKey: "date",
    header: "생성일",
    size: 100,
  },
  {
    accessorKey: "status",
    header: "상태",
    size: 80,
    cell: ({ row }) => {
      const val = row.original.status;
      if (val === "published") {
        return (
          <Badge variant="white" size="sm" className="text-blue-600 border border-blue-200">
            게시됨
          </Badge>
        );
      }
      return (
        <Badge variant="white" size="sm" className="text-gray-600 border border-gray-300">
          초안
        </Badge>
      );
    },
  },
  
  {
    accessorKey: "more",
    header: "",
    size: 32,
    cell: () => {
      return (
        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center justify-center w-full h-full p-1 hover:bg-gray-100 rounded-full cursor-pointer">
            <MoreVertical className="h-5 w-5 text-gray-500" />
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem>편집</DropdownMenuItem>
            <DropdownMenuItem>미리보기</DropdownMenuItem>
            <DropdownMenuItem>복제</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem variant="destructive">삭제</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
  },
];

export default function TeacherDashboardPage() {
  const router = useRouter();

  return (
      <div className="space-y-6">
        <div className="grid grid-cols-1 gap-4 md:grid-cols-4">
          <Card className="p-4">
            <div className="flex justify-between">
              <div>
                <Typography variant="p" size="sm" className="text-gray-500">
                  총 학생 수
                </Typography>
                <Typography variant="h2" size="xl" weight="bold">
                  12
                </Typography>
                <Typography variant="p" size="sm" className="text-green-500">
                  +2 지난 달 대비
                </Typography>
              </div>
              <div className="h-6 w-6 text-gray-400">👥</div>
            </div>
          </Card>

          <Card className="p-4">
            <div className="flex justify-between">
              <div>
                <Typography variant="p" size="sm" className="text-gray-500">
                  생성된 컨텐츠
                </Typography>
                <Typography variant="h2" size="xl" weight="bold">
                  48
                </Typography>
                <Typography variant="p" size="sm" className="text-green-500">
                  +15 지난 달 대비
                </Typography>
              </div>
              <div className="h-6 w-6 text-gray-400">📄</div>
            </div>
          </Card>

          <Card className="p-4">
            <div className="flex justify-between">
              <div>
                <Typography variant="p" size="sm" className="text-gray-500">
                  평균 학습 시간
                </Typography>
                <Typography variant="h2" size="xl" weight="bold">
                  32분
                </Typography>
                <Typography variant="p" size="sm" className="text-green-500">
                  +5 지난 주 대비
                </Typography>
              </div>
              <div className="h-6 w-6 text-gray-400">⏰</div>
            </div>
          </Card>

          <Card className="p-4">
            <div className="flex justify-between">
              <div>
                <Typography variant="p" size="sm" className="text-gray-500">
                  평균 이해도
                </Typography>
                <Typography variant="h2" size="xl" weight="bold">
                  76%
                </Typography>
                <Typography variant="p" size="sm" className="text-green-500">
                  +8% 지난 달 대비
                </Typography>
              </div>
              <div className="h-6 w-6 text-gray-400">📊</div>
            </div>
          </Card>
        </div>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <Card className="p-4">
            <Typography variant="h3" size="lg" weight="bold" className="mb-4">
              최근 학생 활동
            </Typography>
            <div className="flex items-center mb-2">
              <div className="mr-2 h-10 w-10 rounded-full bg-gray-200" />
              <div>
                <Typography variant="p" weight="semibold" className="text-gray-800">
                  김민준
                </Typography>
                <Typography variant="p" size="sm" className="text-gray-500">
                  3학년 - 시각적 처리 어려움 학습 진행 중
                </Typography>
              </div>
              <div className="ml-auto text-sm text-gray-400">2025-03-09</div>
            </div>
            <div className="flex items-center mb-2">
              <div className="mr-2 h-10 w-10 rounded-full bg-gray-200" />
              <div>
                <Typography variant="p" weight="semibold" className="text-gray-800">
                  이서연
                </Typography>
                <Typography variant="p" size="sm" className="text-gray-500">
                  2학년 - 음운적 처리 어려움 학습 진행 중
                </Typography>
              </div>
              <div className="ml-auto text-sm text-gray-400">2025-03-08</div>
            </div>
            <div className="flex items-center">
              <div className="mr-2 h-10 w-10 rounded-full bg-gray-200" />
              <div>
                <Typography variant="p" weight="semibold" className="text-gray-800">
                  박지호
                </Typography>
                <Typography variant="p" size="sm" className="text-gray-500">
                  4학년 - 복합적 어려움 학습 진행 중
                </Typography>
              </div>
              <div className="ml-auto text-sm text-gray-400">2025-03-10</div>
            </div>
          </Card>

          <Card className="p-4">
            <Typography variant="h3" size="lg" weight="bold" className="mb-4">
              학생 진도 현황
            </Typography>
            <div className="mb-2">
              <div className="mb-1 flex justify-between text-sm">
                <span>김민준</span>
                <span>68%</span>
              </div>
              <div className="h-2 rounded bg-gray-200">
                <div className="h-full w-[68%] rounded bg-dyslexia-blue" />
              </div>
            </div>
            <div className="mb-2">
              <div className="mb-1 flex justify-between text-sm">
                <span>이서연</span>
                <span>42%</span>
              </div>
              <div className="h-2 rounded bg-gray-200">
                <div className="h-full w-[42%] rounded bg-dyslexia-blue" />
              </div>
            </div>
            <div className="mb-2">
              <div className="mb-1 flex justify-between text-sm">
                <span>박지호</span>
                <span>75%</span>
              </div>
              <div className="h-2 rounded bg-gray-200">
                <div className="h-full w-[75%] rounded bg-dyslexia-blue" />
              </div>
            </div>
            <div>
              <div className="mb-1 flex justify-between text-sm">
                <span>최예린</span>
                <span>51%</span>
              </div>
              <div className="h-2 rounded bg-gray-200">
                <div className="h-full w-[51%] rounded bg-dyslexia-blue" />
              </div>
            </div>
          </Card>
        </div>

        <Card className="p-4">
          <div className="mb-4 flex items-center justify-between">
            <Typography variant="h3" size="lg" weight="bold">
              최근 생성된 콘텐츠
            </Typography>
            <Button variant="outline" className="px-4" onClick={() => router.navigate({ to: "/teacher/documents" })}>
              + 새 컨텐츠
            </Button>
          </div>
          <DataTable columns={columns} data={contentData} pageSize={5} />
        </Card>
      </div>
  );
}
