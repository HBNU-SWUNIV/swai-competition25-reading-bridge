import { TeacherLayout } from "@/features/layouts";
import { Outlet, createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/teacher")({
  component: TeacherDashboard,
});

export default function TeacherDashboard() {
  return (
    <TeacherLayout>
      <Outlet />
    </TeacherLayout>
  );
}
