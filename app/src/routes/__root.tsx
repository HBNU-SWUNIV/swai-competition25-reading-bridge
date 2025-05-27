import { QueryClientProvider } from "@tanstack/react-query";
import { queryClient } from "@/shared/lib/tanstack-query";
import { createRootRoute, Outlet } from '@tanstack/react-router'
import { ErrorBoundary } from "react-error-boundary";
import { TanStackRouterDevtools } from '@tanstack/router-devtools';
import { Toaster } from 'sonner'

export const Route = createRootRoute({
  component: () => (
    <ErrorBoundary fallback={<div>오류 처리</div>}>
      <QueryClientProvider client={queryClient}>
        <Outlet />
        <TanStackRouterDevtools />
      </QueryClientProvider>
      <Toaster position="top-center" />
    </ErrorBoundary>
  ),
});
