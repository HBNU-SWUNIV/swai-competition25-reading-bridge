import Header from "../header";

export default function BaseLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="h-screen w-[100dvw]">
      {children}
    </div>
  );
}
