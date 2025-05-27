import { StudentLayout } from "@/features/layouts/student-layout";
import { Outlet, createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/student")({
  component: StudentDashboard,
});

export default function StudentDashboard() {
  return (
    <StudentLayout>
      <Outlet />
    </StudentLayout>
  );
}
