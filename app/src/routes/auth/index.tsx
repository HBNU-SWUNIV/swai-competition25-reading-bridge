import { createFileRoute } from '@tanstack/react-router';
import SignupPage from "@page/signup";

export const Route = createFileRoute('/auth/')({
	component: RouteComponent,
});

function RouteComponent() {
	return <SignupPage />;
}
