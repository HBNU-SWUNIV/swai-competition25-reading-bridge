import { ContentManagePage } from '@/page/teacher';
import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/teacher/content')({
	component: RouteComponent,
});

function RouteComponent() {
	return (
		<div>
			<ContentManagePage />
		</div>
	);
}
