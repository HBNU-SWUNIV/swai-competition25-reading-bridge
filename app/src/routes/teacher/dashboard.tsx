import { createFileRoute } from "@tanstack/react-router";
import { TeacherDashboardPage } from '@/page/teacher';

export const Route = createFileRoute("/teacher/dashboard")({
  component: RouteComponent,
});

function RouteComponent() {
  return (
    <div>
        <TeacherDashboardPage />
    </div>
  );
}
