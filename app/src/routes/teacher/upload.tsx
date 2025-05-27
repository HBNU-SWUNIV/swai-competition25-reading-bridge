import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/teacher/upload')({
  component: RouteComponent,
});

function RouteComponent() {
  return <div>Hello "/teacher/upload"!</div>;
}
