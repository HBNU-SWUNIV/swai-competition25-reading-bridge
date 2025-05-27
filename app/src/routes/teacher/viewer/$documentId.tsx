import { useState } from "react"
import { createFileRoute } from "@tanstack/react-router"
import { useQuery } from "@tanstack/react-query"
import { Typography, Button, Select, Card, CardContent } from "@/shared/ui"
import { ChevronLeft, ChevronRight, ZoomIn, ZoomOut, Maximize, Loader2, Info } from "lucide-react"
import { viewerApi } from "@/features/viewer/api/viewer-api"
import { PageTips } from "@/features/viewer/ui/page-tips"
import { PageImages } from "@/features/viewer/ui/page-images"
import { ProcessedContent } from "@/features/viewer/ui/processed-content"

export const Route = createFileRoute('/teacher/viewer/$documentId')({
  component: DocumentViewerPage,
})

function DocumentViewerPage() {
  const { documentId } = Route.useParams()
  const docId = Number.parseInt(documentId)

  const [currentPage, setCurrentPage] = useState(1);
  const [fontSize, setFontSize] = useState(18);
  const [fontFamily, setFontFamily] = useState("PeachMarket");
  const [lineSpacing, setLineSpacing] = useState(1.5);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [showTips, setShowTips] = useState(false);

  const { data: pageContents = [], isLoading: isLoadingPageContent } = useQuery({
    queryKey: ["document", docId, "page-content", currentPage],
    queryFn: () => viewerApi.getPageContent(docId, currentPage),
    enabled: !!docId
  })

  const currentPageContent = pageContents.length > 0 ? pageContents[0] : null

  const { data: pageTips = [] } = useQuery({
    queryKey: ["page", currentPageContent?.id, "tips"],
    queryFn: () => viewerApi.getPageTips(currentPageContent?.id as number),
    enabled: !!currentPageContent?.id && showTips
  })

  const { data: pageImages = [] } = useQuery({
    queryKey: ["page", currentPageContent?.id, "images"],
    queryFn: () => viewerApi.getPageImages(currentPageContent?.id as number),
    enabled: !!currentPageContent?.id
  })

  const documentTitle = currentPageContent?.sectionTitle || '문서 제목'
  const blocks = currentPageContent?.processedContent || []

  const toggleFullscreen = () => {
    if (!window.document.fullscreenElement) {
      window.document.documentElement.requestFullscreen().then(() => {
        setIsFullscreen(true)
      }).catch(() => {})
    } else {
      window.document.exitFullscreen().then(() => {
        setIsFullscreen(false)
      }).catch(() => {})
    }
  }

  const handlePageChange = (newPage: number) => {
    if (newPage > 0) {
      setCurrentPage(newPage)
    }
  }

  const handleFontSizeChange = (increment: boolean) => {
    setFontSize(prev => {
      const newSize = increment ? prev + 1 : prev - 1
      return Math.min(Math.max(newSize, 12), 24)
    })
  }

  return (
    <div className="container mx-auto py-4 px-4">
      <div className="bg-white shadow-md rounded-lg p-4 mb-6 sticky top-0 z-10">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-2">
            <div className="flex items-center gap-1">
              <Button
                variant="outline"
                size="sm"
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage <= 1}
              >
                <ChevronLeft className="w-4 h-4" />
              </Button>
              <Typography variant="p" className="mx-2 text-slate-400">
                {currentPage} 페이지
              </Typography>
              <Button
                variant="outline"
                size="sm"
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={pageContents.length === 0}
              >
                <ChevronRight className="w-4 h-4" />
              </Button>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <Button 
                variant="outline" 
                size="sm" 
                onClick={() => handleFontSizeChange(false)}
                disabled={fontSize <= 12}
              >
                <ZoomOut className="w-4 h-4" />
              </Button>
              <Typography variant="p" className="text-slate-600 font-bold">{fontSize}px</Typography>
              <Button 
                variant="outline" 
                size="sm" 
                onClick={() => handleFontSizeChange(true)}
                disabled={fontSize >= 24}
              >
                <ZoomIn className="w-4 h-4" />
              </Button>
            </div>
            <Select
              value={fontFamily}
              onChange={(e) => setFontFamily(e.target.value)}
              className="w-40"
            >
              <option value="PeachMarket" style={{ fontFamily: 'PeachMarket' }}>피치마켓</option>
              <option value="Noto Sans KR" style={{ fontFamily: 'Noto Sans KR' }}>Noto Sans</option>
              <option value="Lexend" style={{ fontFamily: 'Lexend' }}>Lexend</option>
              <option value="Arial" style={{ fontFamily: 'Arial, sans-serif' }}>Arial</option>
            </Select>
            <Button 
              variant={showTips ? "default" : "outline"}
              size="sm"
              onClick={() => setShowTips(!showTips)}
              className="flex items-center gap-1"
            >
              <Info className="w-4 h-4" />
              <span>핵심 어휘 {showTips ? '숨기기' : '보기'}</span>
            </Button>
            <Button variant="outline" onClick={toggleFullscreen}>
              <Maximize className="w-4 h-4" />
            </Button>
          </div>
        </div>
      </div>

      <Card className="w-full">
        <CardContent className="px-8 py-2">
        <div className="w-full flex justify-end">
        <Typography variant="h3" className="my-2 text-slate-400">
              {documentTitle}
        </Typography>       
        </div>
   
          {isLoadingPageContent ? (
            <div className="flex justify-center items-center py-20">
              <Loader2 className="w-8 h-8 animate-spin" />
              <Typography variant="p" className="ml-2">페이지를 불러오는 중...</Typography>
            </div>
          ) : currentPageContent ? (
            <>
              {blocks.length > 0 && (
                <div className="mb-8">
                  <ProcessedContent
                    blocks={blocks}
                    fontSize={fontSize}
                    fontFamily={fontFamily}
                    lineSpacing={lineSpacing}
                    documentId={docId}
                    pageNumber={currentPage}
                  />
                </div>
              )}
              {showTips && <PageTips tips={pageTips} fontSize={fontSize} />}
            </>
          ) : (
            <Typography variant="p" className="text-center text-gray-500 py-10">
              페이지 내용이 없거나 로드하지 못했습니다.
            </Typography>
          )}
        </CardContent>
      </Card>
      <div className="flex justify-between mt-6 w-full">
        <Button
          variant="outline"
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage <= 1}
          size="lg"
        >
          <ChevronLeft className="w-4 h-4 mr-2" />
          이전 페이지
        </Button>
        <Button
          variant="outline"
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={pageContents.length === 0}
          size="lg"
        >
          다음 페이지
          <ChevronRight className="w-4 h-4 ml-2" />
        </Button>
      </div>
    </div>
  )
} 