import * as React from "react"
import * as ProgressPrimitive from "@radix-ui/react-progress"
import { cn } from "@/lib/utils"

interface ProgressBarProps {
	progress: number;
	maxWidth?: string;
	height?: string;
	className?: string;
	showPercentage?: boolean;
	bgColor?: string;
	progressColor?: string;
}

export const ProgressBar = React.forwardRef<
	React.ElementRef<typeof ProgressPrimitive.Root>,
	ProgressBarProps
>(({ className, progress, ...props }, ref) => (
	<ProgressPrimitive.Root
		ref={ref}
		className={cn(
			"relative h-2 w-full overflow-hidden rounded-full bg-gray-200",
			className
		)}
		{...props}
	>
		<ProgressPrimitive.Indicator
			className="h-full w-full flex-1 bg-blue-500 transition-all"
			style={{ transform: `translateX(-${100 - progress}%)` }}
		/>
	</ProgressPrimitive.Root>
))
ProgressBar.displayName = "ProgressBar"

export function LabeledProgressBar({
	label,
	progress,
	maxWidth = '100%',
	height = '0.75rem',
	className,
	showPercentage = true,
	bgColor = 'bg-gray-200',
	progressColor = 'bg-dyslexia-blue',
}: ProgressBarProps & { label: string }) {
	return (
		<div className={cn('flex flex-col gap-1', className)}>
			<div className="flex justify-between items-center mb-1">
				<span className="text-sm font-medium text-gray-700">{label}</span>
				{showPercentage && (
					<span className="text-sm text-gray-600">
						{Math.min(Math.max(0, progress), 100).toFixed(0)}%
					</span>
				)}
			</div>
			<ProgressBar
				progress={progress}
				maxWidth={maxWidth}
				height={height}
				bgColor={bgColor}
				progressColor={progressColor}
				showPercentage={false}
			/>
		</div>
	);
}
