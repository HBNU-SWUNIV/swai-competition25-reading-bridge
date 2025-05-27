#!/bin/bash

# 스크립트 실행 권한 확인 및 설정
if [ ! -x "$(pwd)/gradlew" ]; then
    echo "gradlew에 실행 권한을 부여합니다..."
    chmod +x ./gradlew
fi

# 환경 변수 파일 확인 및 로드
if [ ! -f ".env" ]; then
    echo "경고: .env 파일이 없습니다. 환경 변수가 설정되어 있지 않을 수 있습니다."
    echo "필요한 환경 변수: POSTGRES_HOST, POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD, KAKAO_CLIENT_ID, KAKAO_CLIENT_SECRET, KAKAO_REDIRECT_URI, JWT_SECRET, OPENAI_API_KEY"
else
    echo ".env 파일에서 환경 변수를 로드합니다..."
    # .env 파일에서 환경 변수 로드
    export $(grep -v '^#' .env | xargs)

    # 필수 환경 변수 확인
    required_vars=("POSTGRES_HOST" "POSTGRES_DB" "POSTGRES_USER" "POSTGRES_PASSWORD" "KAKAO_CLIENT_ID" "KAKAO_CLIENT_SECRET" "KAKAO_REDIRECT_URI" "JWT_SECRET" "OPENAI_API_KEY")
    missing_vars=()

    for var in "${required_vars[@]}"; do
        if [ -z "${!var}" ]; then
            missing_vars+=("$var")
        fi
    done

    if [ ${#missing_vars[@]} -ne 0 ]; then
        echo "경고: 다음 환경 변수가 설정되지 않았습니다: ${missing_vars[*]}"
    else
        echo "모든 필수 환경 변수가 로드되었습니다."
    fi
fi

# 빌드 여부 확인
read -p "애플리케이션을 빌드하시겠습니까? (y/n): " build_choice
if [ "$build_choice" = "y" ] || [ "$build_choice" = "Y" ]; then
    echo "애플리케이션 빌드 중..."
    ./gradlew clean build -x test
    if [ $? -ne 0 ]; then
        echo "빌드 실패. 스크립트를 종료합니다."
        exit 1
    fi
    echo "빌드 완료!"
fi

# 애플리케이션 실행
echo "스프링 애플리케이션 실행 중..."

# 환경 변수를 직접 전달하여 애플리케이션 실행
SPRING_DATASOURCE_URL="jdbc:postgresql://${POSTGRES_HOST}:5432/${POSTGRES_DB}" \
SPRING_DATASOURCE_USERNAME="${POSTGRES_USER}" \
SPRING_DATASOURCE_PASSWORD="${POSTGRES_PASSWORD}" \
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_CLIENT_ID="${KAKAO_CLIENT_ID}" \
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_CLIENT_SECRET="${KAKAO_CLIENT_SECRET}" \
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KAKAO_REDIRECT_URI="${KAKAO_REDIRECT_URI}" \
SPRING_JWT_SECRET="${JWT_SECRET}" \
OPENAI_API_KEY="${OPENAI_API_KEY}" \
./gradlew bootRun

# 종료 시 메시지
echo "애플리케이션이 종료되었습니다."
