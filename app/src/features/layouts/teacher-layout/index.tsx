import { useState } from "react";
import { Sidebar } from "./ui/sidebar";
import { TopHeader } from "./ui/top";

interface TeacherLayoutProps {
  children?: React.ReactNode;
}

export function TeacherLayout({ children }: TeacherLayoutProps) {
  const [open, setOpen] = useState(true);
  return (
    <div className="flex min-h-screen bg-gray-50">
      <Sidebar open={open} onClose={() => setOpen(false)} onOpen={() => setOpen(true)} />
      <div className="flex flex-1 flex-col">
        <TopHeader />
        <main className="flex-1 p-6">{children}</main>
      </div>
    </div>
  );
}
