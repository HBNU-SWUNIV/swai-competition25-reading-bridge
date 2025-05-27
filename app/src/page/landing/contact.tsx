import { Typography } from "@/shared/ui/typography";
import { Button } from "@/shared/ui/button";

export default function Contact() {
    return (
        <section className="bg-dyslexia-blue px-4 py-20 text-center">
            <Typography
                variant="h2"
                size="2xl"
                weight="bold"
                align="center"
                className="mb-4 text-white"
            >
                모든 학생이 배움의 기쁨을 느낄 수 있도록
            </Typography>

            <Typography
                variant="p"
                size="lg"
                align="center"
                className="mx-auto max-w-xl text-white"
            >
                단 2분 만에 첫 맞춤형 학습 자료를 만들어 보세요
            </Typography>

            <div className="mt-6">
                <Button
                    variant="default"
                    size="lg"
                    className="w-[234px] h-[40px] bg-white text-black hover:bg-white/90"
                >
                    문의하기
                </Button>
            </div>
        </section>
    );
}