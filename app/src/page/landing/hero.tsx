import { Button } from "@/shared/ui/button";
import { Typography } from "@/shared/ui/typography";
import { useRouter } from "@tanstack/react-router";

export default function Hero() {
  const router = useRouter();
  return (
    <section className="w-full pt-32 flex justify-center items-center bg-white">
      <div className="max-w-[768px] w-full flex gap-8">
        <div className="flex flex-col gap-6 pt-12 flex-[0.4]">
          <div className="flex flex-col gap-4">
            <Typography
              as="h1"
              weight="bold"
              variant="h1"
              color="primary"
              className="!leading-tight text-5xl"
            >
              리딩브릿지
            </Typography>
            <Typography
              as="h2"
              variant="h2"
              weight="bold"
              className="text-slate-600 text-2xl leading-8"
            >
              난독증 학생들을 위한 <br />
              맞춤형 학습 경험
            </Typography>
            <Typography
              color="secondary"
              size="lg"
              className="!leading-tight text-slate-500"
            >
              교사가 업로드한 PDF 교안을 AI로 변환, 모든 학생이 쉽게 배울 수
              있도록
            </Typography>
          </div>

          <div className="flex flex-col gap-3">
            <Button
              size="xl"
              onClick={() => router.navigate({ to: "/teacher/documents" })}
            >
              교사로 시작하기
            </Button>
            <Button variant="outline" size="xl">
              학생으로 시작하기
            </Button>
          </div>
        </div>

        <div className="w-full flex-[0.6] flex justify-center items-center flex-col aspect-square gap-y-6">
          <div className="relative">
            <img
              src="/images/service-screen-0.png"
              alt="리딩브릿지 미리보기"
              className="w-full object-contain"
            />
            <div className="absolute bottom-4 left-6">
              <Typography as="p" size="lg" className="text-white">
                기존 교안을 난독증 학생용으로 쉽게 변환하세요.
              </Typography>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
