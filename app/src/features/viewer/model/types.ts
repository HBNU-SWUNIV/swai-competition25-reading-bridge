export type Block =
  | {
      id: string
      type: 'TEXT'
      text: string
      blank?: boolean
    }
  | {
      id: string
      type: 'HEADING1' | 'HEADING2' | 'HEADING3'
      text: string
    }
  | {
      id: string
      type: 'LIST' | 'DOTTED'
      items: string[]
    }
  | {
      id: string
      type: 'IMAGE'
      url: string
      alt: string
      width?: number
      height?: number
    }
  | {
      id: string
      type: 'TABLE'
      headers: string[]
      rows: string[][]
    }
  | {
      id: string
      type: 'PAGE_TIP'
      tipId: string
    }
  | {
      id: string
      type: 'PAGE_IMAGE'
      imageId?: string
      url: string;
      alt?: string
      prompt?: string
      concept: string
    }

export interface PageBlockApiResponse {
  pageId: string
  title: string
  blocks: Block[]
  createdAt: string
  updatedAt: string
}

export interface Document {
  id: number
  title: string
  totalPages: number
  grade?: string
  subjectPath?: string
}

export interface PageContentResponse {
  id: number
  documentId: number
  pageNumber: number
  originalContent: string
  processedContent: Block[]
  processingStatus: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  sectionTitle: string
  readingLevel: number
  wordCount: number
  complexityScore: number
  createdAt: string
  updatedAt: string
}

export interface PageTipResponse {
  id: number
  pageId: number
  term: string
  simplifiedExplanation: string
  termPosition: { start: number; end: number } | null
  termType: 'DIFFICULT_WORD' | 'COMPLEX_CONCEPT' | 'ABSTRACT_IDEA' | 'TECHNICAL_TERM'
  visualAidNeeded: boolean
  readAloudText: string
  createdAt: string
  updatedAt: string
}

export interface PageImageResponse {
  id: number
  pageId: number
  imageUrl: string
  imageType: 'CONCEPT_VISUALIZATION' | 'DIAGRAM' | 'COMPARISON_CHART' | 'EXAMPLE_ILLUSTRATION'
  conceptReference: string
  altText: string
  positionInPage: { x: number; y: number } | null
  createdAt: string
  updatedAt: string
} 