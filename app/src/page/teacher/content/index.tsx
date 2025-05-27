import { Card } from "@/shared/ui/card";
import { Button } from "@/shared/ui/button";
import { Typography } from "@/shared/ui/typography";
import { DataTable } from "@/shared/ui/table";
import { DropdownMenu, DropdownMenuTrigger, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator } from "@/shared/ui/dropdown-menu";
import { MoreVertical } from "lucide-react";
import type { ColumnDef } from "@tanstack/react-table";
import { useRouter } from "@tanstack/react-router";

interface ContentItem {
  id: string;
  title: string;
  subject: string;
  grade: string;
  updatedAt: string;
  status: "draft" | "published";
}

const contentData: ContentItem[] = [
  {
    id: "1",
    title: "생태계와 환경",
    subject: "과학",
    grade: "3학년",
    updatedAt: "2025-03-10",
    status: "published",
  },
  {
    id: "2",
    title: "우리 동네 탐험",
    subject: "사회",
    grade: "2학년",
    updatedAt: "2025-03-09",
    status: "draft",
  },
];

const columns: ColumnDef<ContentItem>[] = [
  {
    accessorKey: "title",
    header: "제목",
    size: 150,
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
    size: 80,
    cell: ({ row }) => (
      <div className="text-center">{row.original.grade}</div>
    ),
  },
  {
    accessorKey: "updatedAt",
    header: "최근 수정일",
    size: 100,
  },
  {
    accessorKey: "status",
    header: "상태",
    size: 80,
    cell: ({ row }) => {
      const val = row.original.status;
      return val === "published" ? (
        <span className="rounded bg-blue-100 px-2 py-1 text-xs text-blue-600">
          게시됨
        </span>
      ) : (
        <span className="rounded bg-gray-100 px-2 py-1 text-xs text-gray-600">
          초안
        </span>
      );
    },
  },
  {
    accessorKey: "actions",
    header: "",
    size: 32,
    cell: () => {
      return (
        <DropdownMenu>
          <DropdownMenuTrigger className="flex items-center justify-center w-full h-full p-1 hover:bg-gray-50 rounded-full cursor-pointer">
            <MoreVertical className="h-5 w-5 text-gray-500" />
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem>편집</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem variant="destructive">삭제</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
  },
];

export default function ContentManagePage() {
	const router = useRouter();

  return (
      <div className="space-y-6">
        <Card className="p-6">
          <Typography variant="h2" size="xl" weight="bold" className="mb-2">
            컨텐츠 관리
          </Typography>
          <Typography variant="p" size="base" className="text-gray-600 mb-4">
            학생들을 위한 맞춤형 학습 컨텐츠를 관리하세요.
          </Typography>
          <div className="flex justify-end">
            <Button variant="default" size="lg" onClick={() => router.navigate({ to: "/teacher/documents" })}>
              + 새 컨텐츠
            </Button>
          </div>
        </Card>

        <Card className="p-6">
          <DataTable columns={columns} data={contentData} pageSize={5} />
        </Card>
      </div>
  );
}
