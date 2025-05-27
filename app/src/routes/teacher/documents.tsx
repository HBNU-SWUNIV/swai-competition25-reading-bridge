import { useState, useEffect } from 'react'
import { createFileRoute } from '@tanstack/react-router'
import { useNavigate } from '@tanstack/react-router'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import {
  createColumnHelper,
  flexRender,
  getCoreRowModel,
  getSortedRowModel,
  getPaginationRowModel,
  useReactTable,
  type SortingState,
} from '@tanstack/react-table'
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableHead, 
  TableHeader, 
  TableRow 
} from '@/shared/ui/table'
import { Button } from '@/shared/ui/button'
import { Input } from '@/shared/ui/input'
import { Select } from '@/shared/ui/select'
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/ui/card'
import { ProgressBar } from '@/shared/ui/progress-bar'
import { Show, Typography } from '@/shared/ui'
import { axiosClient } from '@/shared/api/axios'
import { useAuth } from '@/shared/hooks/use-auth'
import { toast } from 'sonner'

export const Route = createFileRoute('/teacher/documents')({
  component: DocumentsPage,
})

// 문서 처리 상태 타입
type ProcessStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'

// 학년 타입
type Grade = 'GRADE_1' | 'GRADE_2' | 'GRADE_3' | 'GRADE_4' | 'GRADE_5' | 'GRADE_6'

// 문서 타입 정의
interface Document {
  id: number
  teacherId: number
  title: string
  originalFilename: string
  fileSize?: number
  pageCount?: number
  grade?: Grade
  subjectPath?: string
  processStatus: ProcessStatus
  createdAt: string
  updatedAt: string
}

// 문서 응답 타입
interface DocumentResponseDto {
  success: boolean
  message: string
  document: Document
}

// 문서 처리 상태 응답 타입
interface DocumentProcessStatusDto {
  success: boolean
  message: string
  documentId: number
  status: ProcessStatus
  progress: number
}

// 학생 문서 목록 응답 타입
interface DocumentListResponseDto {
  success: boolean
  message: string
  documents: Document[]
}

// 파일 업로드 폼 데이터
interface UploadFormData {
  file: File | null
  title: string
  grade: Grade
  subjectPath: string
}

// API 호출 함수들
const documentApi = {
  uploadDocument: async (teacherId: number, formData: FormData): Promise<DocumentResponseDto> => {
    const { data } = await axiosClient.post<DocumentResponseDto>('/documents/upload', formData, {
      params: {
        teacherId,
      },
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return data
  },

  getTeacherDocuments: async (teacherId: number): Promise<Document[]> => {
    const data = await axiosClient.get(`/teacher/documents/${teacherId}`) as unknown as DocumentListResponseDto;
    return data.documents;
  },

  getDocumentStatus: async (documentId: number): Promise<DocumentProcessStatusDto> => {
    const data = await axiosClient.get(`/documents/status/${documentId}`) as unknown as DocumentProcessStatusDto;
    return data;
  },

  retryProcessing: async (documentId: number): Promise<DocumentProcessStatusDto> => {
    const data = await axiosClient.post(`/documents/status/${documentId}/retry`) as unknown as DocumentProcessStatusDto;
    return data;
  },
}

// 상태 배지 컴포넌트
function StatusBadge({ status }: { status: ProcessStatus }) {
  let color = ''
  let label = ''

  switch (status) {
    case 'PENDING':
      color = 'bg-yellow-100 text-yellow-800'
      label = '대기중'
      break
    case 'PROCESSING':
      color = 'bg-blue-100 text-blue-800'
      label = '처리중'
      break
    case 'COMPLETED':
      color = 'bg-green-100 text-green-800'
      label = '완료'
      break
    case 'FAILED':
      color = 'bg-red-100 text-red-800'
      label = '실패'
      break
  }

  return <span className={`px-2 py-1 rounded-full text-xs font-medium ${color}`}>{label}</span>
}

// 학년 표시 변환 함수
function formatGrade(grade?: Grade): string {
  if (!grade) return '미지정'
  
  const gradeMap: Record<Grade, string> = {
    'GRADE_1': '1학년',
    'GRADE_2': '2학년',
    'GRADE_3': '3학년',
    'GRADE_4': '4학년',
    'GRADE_5': '5학년',
    'GRADE_6': '6학년',
  }
  
  return gradeMap[grade] || grade
}

function DocumentsPage() {
  const { my } = useAuth()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [sorting, setSorting] = useState<SortingState>([])
  const teacherId = my?.id || 0

  const { data: documents = [], isLoading } = useQuery({
    queryKey: ['teacher', teacherId, 'documents'],
    queryFn: () => documentApi.getTeacherDocuments(teacherId),
    enabled: !!teacherId,
  });

  // 문서 처리 상태 폴링
  const [processingDocuments, setProcessingDocuments] = useState<Set<number>>(new Set())
  
  useEffect(() => {
    const processing = new Set<number>()
    for (const doc of documents) {
      if (doc.processStatus === 'PENDING' || doc.processStatus === 'PROCESSING') {
        processing.add(doc.id)
      }
    }
    setProcessingDocuments(processing)
  }, [documents])

  const { data: documentStatuses = {} } = useQuery({
    queryKey: ['documents', 'status', Array.from(processingDocuments)],
    queryFn: async () => {
      const statuses: Record<number, DocumentProcessStatusDto> = {}
      for (const docId of processingDocuments) {
        try {
          const status = await documentApi.getDocumentStatus(docId)
          statuses[docId] = status
          
          // 처리가 완료되거나 실패한 경우 목록 갱신 및 폴링 대상에서 제외
          if (status.status === 'COMPLETED' || status.status === 'FAILED') {
            setProcessingDocuments(prev => {
              const updated = new Set(prev)
              updated.delete(docId)
              return updated
            })
            queryClient.invalidateQueries({ queryKey: ['teacher', teacherId, 'documents'] })
          }
        } catch (error) {
          console.error(`Failed to get status for document ${docId}:`, error)
        }
      }
      return statuses
    },
    enabled: processingDocuments.size > 0,
    refetchInterval: processingDocuments.size > 0 ? 5000 : false,
  })

  // 문서 재처리 뮤테이션
  const retryMutation = useMutation({
    mutationFn: documentApi.retryProcessing,
    onSuccess: (data) => {
      toast.success('문서 처리가 다시 시작되었습니다.')
      // 재처리가 시작된 문서를 폴링 대상에 추가
      setProcessingDocuments(prev => {
        const updated = new Set(prev)
        updated.add(data.documentId)
        return updated
      })
      queryClient.invalidateQueries({ queryKey: ['teacher', teacherId, 'documents'] })
    },
    onError: () => {
      toast.error('문서 재처리 요청에 실패했습니다.')
    },
  })

  // 문서 업로드 뮤테이션
  const uploadMutation = useMutation({
    mutationFn: (formData: FormData) => documentApi.uploadDocument(teacherId, formData),
    onSuccess: (data) => {
      toast.success('PDF 문서가 업로드되었습니다.')
      // 폴링 대상에 새 문서 추가
      setProcessingDocuments(prev => {
        const updated = new Set(prev)
        updated.add(data.document.id)
        return updated
      })
      queryClient.invalidateQueries({ queryKey: ['teacher', teacherId, 'documents'] })
      resetForm()
    },
    onError: (error) => {
      console.error(error);
      toast.error('PDF 업로드에 실패했습니다.')
    },
  })

  // 업로드 폼 상태
  const [uploadForm, setUploadForm] = useState<UploadFormData>({
    file: null,
    title: '',
    grade: 'GRADE_3',
    subjectPath: '',
  })

  // 폼 리셋
  const resetForm = () => {
    setUploadForm({
      file: null,
      title: '',
      grade: 'GRADE_3',
      subjectPath: '',
    })
  }

  // 파일 선택 핸들러
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null
    setUploadForm({ ...uploadForm, file })
    
    // 파일명을 제목으로 사용 (확장자 제외)
    if (file && !uploadForm.title) {
      const fileName = file.name.replace(/\.[^/.]+$/, '')
      setUploadForm(prev => ({ ...prev, title: fileName }))
    }
  }

  // 업로드 폼 제출 핸들러
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!uploadForm.file) {
      toast.error('PDF 파일을 선택해주세요.')
      return
    }
    
    if (!uploadForm.title.trim()) {
      toast.error('문서 제목을 입력해주세요.')
      return
    }
    
    const formData = new FormData()
    formData.append('file', uploadForm.file)
    formData.append('title', uploadForm.title)
    formData.append('grade', uploadForm.grade)
    
    if (uploadForm.subjectPath) {
      formData.append('subjectPath', uploadForm.subjectPath)
    }
    
    uploadMutation.mutate(formData)
  }

  // 문서 뷰어로 이동 핸들러
  const handleViewDocument = (documentId: number) => {
    navigate({ 
      to: '/teacher/viewer/$documentId',
      params: { documentId: String(documentId) } 
    })
  }

  // 재처리 핸들러
  const handleRetry = (documentId: number) => {
    retryMutation.mutate(documentId)
  }

  // 테이블 컬럼 정의
  const columnHelper = createColumnHelper<Document>()
  const columns = [
    columnHelper.accessor('title', {
      header: '제목',
      cell: info => <div className="font-medium">{info.getValue()}</div>,
    }),
    columnHelper.accessor('originalFilename', {
      header: '파일명',
      cell: info => info.getValue(),
    }),
    columnHelper.accessor('grade', {
      header: '학년',
      cell: info => formatGrade(info.getValue()),
    }),
    columnHelper.accessor('pageCount', {
      header: '페이지',
      cell: info => info.getValue() || '-',
    }),
    columnHelper.accessor('processStatus', {
      header: '상태',
      cell: info => <StatusBadge status={info.getValue()} />,
    }),
    columnHelper.accessor('id', {
      header: '진행률',
      cell: info => {
        const docId = info.getValue()
        const doc = info.row.original
        const status = documentStatuses[docId]
        
        if (doc.processStatus === 'PENDING' || doc.processStatus === 'PROCESSING') {
          return (
            <ProgressBar 
              progress={status?.progress ?? 0} 
              className="h-2 w-full"
            />
          )
        }
        
        if (doc.processStatus === 'FAILED') {
          return (
            <Button 
              variant="outline" 
              size="sm" 
              onClick={() => handleRetry(docId)}
              className="text-xs"
            >
              재시도
            </Button>
          )
        }
        
        return null
      },
    }),
    columnHelper.accessor('createdAt', {
      header: '등록일',
      cell: info => new Date(info.getValue()).toLocaleDateString(),
    }),
    columnHelper.accessor(row => row, {
      id: 'actions',
      header: '작업',
      cell: info => {
        const doc = info.getValue()
        const canView =
          doc.processStatus === 'COMPLETED' ||
          (doc.processStatus === 'PROCESSING' && doc.pageCount && doc.pageCount > 0)
        return (
          <div className="flex gap-2">
            <Show when={!!canView}>
              <Button 
                variant="default" 
                size="sm" 
                onClick={() => handleViewDocument(doc.id)}
                className="text-xs"
              >
                보기
              </Button>
            </Show>
          </div>
        )
      },
    }),
  ]

  // 테이블 인스턴스
  const table = useReactTable({
    data: documents,
    columns,
    state: {
      sorting,
    },
    onSortingChange: setSorting,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
  })

  return (
    <div className="container mx-auto py-8 px-4">
      <div className="flex flex-col gap-8">
        {/* 업로드 폼 */}
        <Card>
          <CardHeader>
            <CardTitle>PDF 자료 업로드</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <label htmlFor="file" className="block t요ext-sm font-medium">
                  PDF 파일
                </label>
                <Input
                  id="file"
                  type="file"
                  accept="application/pdf"
                  onChange={handleFileChange}
                  className="w-full"
                />
              </div>
              
              <div className="space-y-2">
                <label htmlFor="title" className="block text-sm font-medium">
                  문서 제목
                </label>
                <Input
                  id="title"
                  value={uploadForm.title}
                  onChange={e => setUploadForm({ ...uploadForm, title: e.target.value })}
                  placeholder="문서 제목을 입력하세요"
                  className="w-full"
                />
              </div>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* <div className="space-y-2">
                  <label htmlFor="grade" className="block text-sm font-medium">
                    학년
                  </label>
                  <Select
                    id="grade"
                    value={uploadForm.grade}
                    onChange={e => setUploadForm({ ...uploadForm, grade: e.target.value as Grade })}
                    className="w-full"
                  >
                    <option value="GRADE_1">1학년</option>
                    <option value="GRADE_2">2학년</option>
                    <option value="GRADE_3">3학년</option>
                    <option value="GRADE_4">4학년</option>
                    <option value="GRADE_5">5학년</option>
                    <option value="GRADE_6">6학년</option>
                  </Select>
                </div> */}
                
                {/* <div className="space-y-2">
                  <label htmlFor="subjectPath" className="block text-sm font-medium">
                    과목 경로 (선택)
                  </label>
                  <Input
                    id="subjectPath"
                    value={uploadForm.subjectPath}
                    onChange={e => setUploadForm({ ...uploadForm, subjectPath: e.target.value })}
                    placeholder="예: science/ecology"
                    className="w-full"
                  />
                </div> */}
              </div>
              
              <div className="flex justify-end">
                <Button 
                  type="submit" 
                  disabled={uploadMutation.isPending || !uploadForm.file || !uploadForm.title.trim()}
                >
                  {uploadMutation.isPending ? '업로드 중...' : 'PDF 업로드'}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
        
        {/* 문서 목록 */}
        <Card>
          <CardHeader>
            <CardTitle>PDF 자료 목록</CardTitle>
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <div className="text-center py-4">
                <Typography variant="p">로딩 중...</Typography>
              </div>
            ) : documents.length === 0 ? (
              <div className="text-center py-4">
                <Typography variant="p">업로드된 PDF 자료가 없습니다.</Typography>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader>
                    {table.getHeaderGroups().map(headerGroup => (
                      <TableRow key={headerGroup.id}>
                        {headerGroup.headers.map(header => (
                          <TableHead key={header.id}>
                            {header.isPlaceholder
                              ? null
                              : flexRender(
                                  header.column.columnDef.header,
                                  header.getContext()
                                )}
                          </TableHead>
                        ))}
                      </TableRow>
                    ))}
                  </TableHeader>
                  <TableBody>
                    {table.getRowModel().rows.map(row => (
                      <TableRow key={row.id}>
                        {row.getVisibleCells().map(cell => (
                          <TableCell key={cell.id}>
                            {flexRender(cell.column.columnDef.cell, cell.getContext())}
                          </TableCell>
                        ))}
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
                
                {/* 페이지네이션 */}
                <div className="flex items-center justify-between mt-4">
                  <div className="flex items-center gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => table.setPageIndex(0)}
                      disabled={!table.getCanPreviousPage()}
                    >
                      처음
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => table.previousPage()}
                      disabled={!table.getCanPreviousPage()}
                    >
                      이전
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => table.nextPage()}
                      disabled={!table.getCanNextPage()}
                    >
                      다음
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => table.setPageIndex(table.getPageCount() - 1)}
                      disabled={!table.getCanNextPage()}
                    >
                      마지막
                    </Button>
                  </div>
                  <span className="text-sm text-gray-700">
                    페이지 {table.getState().pagination.pageIndex + 1} / {table.getPageCount()}
                  </span>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  )
} 