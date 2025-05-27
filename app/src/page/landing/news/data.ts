export type NewsDetails = {
  url: string;
  title: string;
  description: string;
  year: number;
}

export const newsData: NewsDetails[] = [
  {
    url: '',
    title: '난독·경계선 학생 10배 늘었는데…예산 ‘반토막’',
    description: '전지역 예산 30-50% 삭감, 난독협회등록 치료사 부족',
    year: 25,
  },
  {
    url: '',
    title: '난독증 학생 서울서만 3년 새 8배 늘어…"예산 늘려 영유아기부터 치료해야"',
    description: '난독학생이 2020년부터  2023년까지  958명으로 약 8.5배 ↑',
    year: 23,
  },
  {
    url: '',
    title: '늘어나는 난독·경계선 지능학생…조기 발견해야 치료 수월',
    description: '아이들이 어른들의 케어를 못 받는 시간 증가',
    year: 23,
  }
];