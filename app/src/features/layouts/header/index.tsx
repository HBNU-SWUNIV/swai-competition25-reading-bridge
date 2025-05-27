import { Button } from "@/shared/ui/button";
import { Show, Typography } from "@/shared/ui";
import { useAuth } from "@/shared/hooks/use-auth";


type HeaderProps = {
  onAuthClick: () => void
}

export default function Header({ onAuthClick }: HeaderProps) {
  const { isAuthenticated, my, logout } = useAuth();

  const onClickLogout = () => {
    logout();
  }

  return (
    <header className="w-full min-h-[64px] flex justify-center">
      <section className="max-w-[768px] w-full flex items-center justify-between">
        <Typography variant="h2" weight="bold" size="2xl" color="primary">
          리딩브릿지
          </Typography>

        <Show when={!isAuthenticated}>
          <nav className="flex gap-2">
            <Button variant="default" onClick={onAuthClick}>로그인</Button>
            <Button variant="outline" onClick={onAuthClick}>회원가입</Button>
          </nav>
        </Show>

        <Show when={isAuthenticated}>
        <nav className="flex gap-2 items-center">
          <Typography variant="p" color="destructive">
            안녕하세요,
          </Typography>
          <Typography variant="p" color="primary">
            {my?.name}
          </Typography>
          <Typography variant="p" color="destructive">
            님
          </Typography>

          <Button 
            variant="default" 
            onClick={onClickLogout}
            className="ml-2"
          >
              로그아웃
          </Button>
        </nav>
        </Show>

      </section>
    </header>
  );
}
