import { Card, CardContent, Typography } from "@/shared/ui";
import { newsData } from "./data";

export default function News() {
  return (
    <section className="w-full flex flex-col justify-center gap-y-6 items-center bg-white">
      <Typography as="h2" variant="h2" weight="bold" color="primary" className="text-3xl mb-8 leading-8">
        모든 학생은 배울 권리가 있습니다.
      </Typography>


      <nav className="flex flex-col gap-y-3">
        {newsData.map(news => (
          <Card className="w-full flex" key={news.title}>
            <CardContent className="flex items-end justify-between">
              <section className="flex flex-col gap-y-1">
                <Typography as="h3" variant="h3" weight="bold" className="text-xl text-slate-600">
                  {news.title}
                </Typography>
                <Typography as="p" variant="p" className="text-md">
                  {news.description}
                </Typography>
              </section>

              <Typography variant="p" className="text-md text-slate-800">
                {news.year}년 국내기사
              </Typography>

            </CardContent>
          </Card>
        ))}
      </nav>
    </section>
  );
}
