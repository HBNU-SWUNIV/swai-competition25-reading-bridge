import { Typography } from "@/shared/ui/typography";
import { Card } from "@/shared/ui/card";

export default function Introsection() {
    return (
        <section className="px-4 py-16 md:py-24">
            <div className="mx-auto max-w-screen-md text-center mb-10">
                <Typography variant="p" size="base" weight="normal" align="center" className="text-gray-700 leading-relaxed">
                    난독증(읽기 장애)을 겪는 아동이 증가하고 있지만,<br />
                    이를 효과적으로 지원하는 데는 여전히 현실적인 어려움이 많습니다.
                    <br />
                    <br />
                    “맞춤형 교육 자료 부족”<br />
                    “부족한 치료전문가”
                </Typography>
            </div>

            <div className="mx-auto mb-12 text-center">
                <Typography variant="h2" size="2xl" weight="bold" align="center">
                    리딩브릿지를 소개합니다
                </Typography>
            </div>

            <div className="mx-auto grid max-w-screen-xl grid-cols-1 gap-6 px-4 md:grid-cols-3">
                <Card className="rounded-lg bg-white p-6 shadow-sm">
                    <img
                        src="/images/intro1.png"
                        alt="맞춤형 학습자료 생성"
                        className="mb-4 h-auto w-full object-cover"
                    />
                    <Typography variant="h4" className="mb-2 text-gray-800">
                        맞춤형 학습자료 생성
                    </Typography>
                    <Typography variant="p" className="text-gray-600">
                        난독 아동의 수준과 겪는 불편함을 기반으로 기존 교안 자료를
                        아동의 수준에 맞춘 컨텐츠로 자동 변환합니다.
                    </Typography>
                </Card>

                <Card className="rounded-lg bg-white p-6 shadow-sm">
                    <img
                        src="/images/intro2.png"
                        alt="교사 대시보드 지원"
                        className="mb-4 h-auto w-full object-cover"
                    />
                    <Typography variant="h4" className="mb-2 text-gray-800">
                        교사 대시보드 지원
                    </Typography>
                    <Typography variant="p" className="text-gray-600">
                        학생들의 학습 진행 상황을 실시간으로 확인하고, 학습의 어려움을 파악해
                        적확한 피드백을 수행할 수 있습니다.
                    </Typography>
                </Card>

                <Card className="rounded-lg bg-white p-6 shadow-sm">
                    <img
                        src="/images/intro1.png"
                        alt="맞춤형 학습자료 생성"
                        className="mb-4 h-auto w-full object-cover"
                    />
                    <Typography variant="h4" className="mb-2 text-gray-800">
                        맞춤형 학습자료 생성
                    </Typography>
                    <Typography variant="p" className="text-gray-600">
                        난독 아동의 수준과 겪는 불편함을 기반으로 기존 교안 자료를
                        아동의 수준에 맞춘 컨텐츠로 자동 변환합니다.
                    </Typography>
                </Card>
            </div>
        </section>
    );
}
