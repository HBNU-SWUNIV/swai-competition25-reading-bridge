import { Badge, DataTable, DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuSeparator, DropdownMenuTrigger, ProgressBar } from '@/shared/ui';
import { MoreVertical } from 'lucide-react';

import type { ColumnDef } from '@tanstack/react-table';

type Person = {
	id: string;
	name: string;
	grade: number;
	reason: string;
	interests: string[];
	progress: number;
};

const data: Person[] = [
	{
		id: '1',
		name: '박지원',
		grade: 2,
		reason: '시각적 처리 어려움',
		interests: ['우주', '공룡'],
		progress: 20,
	},
	{
		id: '2',
		name: '최성원',
		grade: 3,
		reason: '시각적 처리 어려움',
		interests: ['우주', '공룡'],
		progress: 50,
	},
	{
		id: '3',
		name: '김민준',
		grade: 4,
		reason: '시각적 처리 어려움',
		interests: ['우주', '공룡'],
		progress: 51,
	},
];

const columns: ColumnDef<Person>[] = [
	{
		accessorKey: 'name',
		header: '이름',
		size: 50,
		maxSize: 70,
	},
	{
		accessorKey: 'grade',
		header: '학년',
		cell: ({ row }) => (
			<div className="text-center">{row.original.grade}학년</div>
		),
		size: 60,
		maxSize: 70,
	},
	{
		accessorKey: 'reason',
		header: '난독 유형',
		size: 100,
		maxSize: 100,
	},
	{
		accessorKey: 'progress',
		header: '진행도',
		cell: ({ row }) => (
			<ProgressBar
				progress={row.original.progress}
				maxWidth="100%"
				height="0.75rem"
				showPercentage
			/>
		),
		size: 100,
		maxSize: 100,
	},
	{
		accessorKey: 'interests',
		header: '관심사',
		cell: ({ row }) => (
			<div className="flex flex-wrap gap-2 w-full">
				{row.original.interests.map((interest) => (
					<Badge variant="white" size="md" key={interest}>
						{interest}
					</Badge>
				))}
			</div>
		),
		size: 100,
		maxSize: 100,
	},
	{
		accessorKey: 'more',
		header: '더보기',
		size: 32,
		maxSize: 32,
		cell: ({ row }) => (
			<DropdownMenu>
				<DropdownMenuTrigger className="flex items-center justify-center w-full h-full cursor-pointer hover:bg-gray-100 rounded-full p-1">
					<MoreVertical className="h-5 w-5 text-gray-500" />
				</DropdownMenuTrigger>
				<DropdownMenuContent align="end">
					<DropdownMenuItem>
						프로필 보기
					</DropdownMenuItem>
					<DropdownMenuItem>
						학습 자료 생성
					</DropdownMenuItem>
					<DropdownMenuItem>
						진도 보고서
					</DropdownMenuItem>
					<DropdownMenuSeparator />
					<DropdownMenuItem variant="destructive">
						삭제
					</DropdownMenuItem>
				</DropdownMenuContent>
			</DropdownMenu>
		),
	},
];

export default function StudentPage() {
	return (
		<div className="container mx-auto py-10">
			<DataTable columns={columns} data={data} />
		</div>
	);
}
