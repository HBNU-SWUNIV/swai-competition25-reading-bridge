import { StudentPage } from '@/page/teacher';
import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/teacher/student')({
	component: RouteComponent,
});

function RouteComponent() {
	return (
		<div>
			<StudentPage />
		</div>
	);
}
