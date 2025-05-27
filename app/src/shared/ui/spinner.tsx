import { cn } from '@/lib/utils'

interface SpinnerProps {
  className?: string
}

export const Spinner = ({ className }: SpinnerProps) => {
  return (
    <div
      className={cn(
        'animate-spin rounded-full border-4 border-gray-200 border-t-blue-500',
        className
      )}
    />
  )
} 