# 페이지 문서 조회 API 명세

이 문서는 프런트엔드에서 페이지 문서(Block 기반) 데이터를 활용할 수 있도록 상세 구조와 예시를 제공합니다.

## 엔드포인트 예시
/api/documents-contents/page?documentId={documentId}&page={page}

## 응답 구조

```json
{
  "pageId": "string",         // 페이지 고유 ID
  "title": "string",          // 페이지 제목
  "blocks": [ ... ],           // Block 배열(아래 Block 구조 참고)
  "createdAt": "ISO8601",    // 생성일시
  "updatedAt": "ISO8601"     // 수정일시
}
```

### blocks: Block[]
- BlockType, 필드, 예시는 BLOCK_GUIDE.md 참고
- 각 Block은 type에 따라 필요한 필드를 포함

| type         | 필드명/타입                | 설명                        |
|--------------|---------------------------|-----------------------------|
| TEXT         | text: string              | 본문 텍스트                 |
| HEADING1~3   | text: string              | 제목 텍스트                 |
| LIST/DOTTED  | items: string[]           | 리스트 항목                 |
| IMAGE        | url: string               | 이미지 URL                  |
|              | alt: string               | 대체 텍스트                 |
|              | width: number (optional)  | 이미지 너비(px)             |
|              | height: number (optional) | 이미지 높이(px)             |
| TABLE        | headers: string[]         | 표 헤더                     |
|              | rows: string[][]          | 표 데이터                   |
| PAGE_TIP     | tipId: string             | 용어설명/팁의 고유 ID       |
| PAGE_IMAGE   | imageId: string           | 이미지의 고유 ID            |

## 전체 응답 예시

```json
{
  "pageId": "page-123",
  "title": "생태계와 환경",
  "blocks": [
    { "id": "1", "type": "HEADING1", "text": "생태계와 환경" },
    { "id": "2", "type": "TEXT", "text": "생태계는 생물과 환경으로 이루어져 있습니다." },
    { "id": "3", "type": "LIST", "items": ["생물", "환경", "상호작용"] },
    { "id": "4", "type": "IMAGE", "url": "https://example.com/image1.png", "alt": "생태계 구성도", "width": 400, "height": 300 },
    { "id": "5", "type": "TABLE", "headers": ["구성요소", "설명"], "rows": [["생물", "동식물 등"], ["환경", "물, 공기 등"]] },
    { "id": "6", "type": "PAGE_TIP", "tipId": "tip-uuid-123" },
    { "id": "7", "type": "PAGE_IMAGE", "imageId": "img-uuid-456" }
  ],
  "createdAt": "2024-05-01T12:00:00Z",
  "updatedAt": "2024-05-01T12:00:00Z"
}
```

## 참고
- Block 구조 상세는 BLOCK_GUIDE.md를 참고하세요.
- type 값은 반드시 대문자(예: "TEXT", "HEADING1")로 작성됩니다.
- 불필요한 설명, 마크다운, 코드블록 없이 JSON만 반환됩니다. 