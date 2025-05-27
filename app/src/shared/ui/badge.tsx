import type { KeyboardEvent, ReactNode } from 'react';

export interface BadgeProps {
	children: ReactNode;
	variant?: 'disabled' | 'blue' | 'white';
	size?: 'sm' | 'md' | 'lg';
	className?: string;
	onClick?: () => void;
	onKeyDown?: (event: KeyboardEvent) => void;
}

export function Badge({
	children,
	variant = 'blue',
	size = 'md',
	className = '',
	onClick,
	onKeyDown,
}: BadgeProps) {
	const baseClasses =
		'inline-flex items-center justify-center rounded-full font-medium';

	const sizeClasses = {
		sm: 'text-xs px-2 py-0.5',
		md: 'text-sm px-2.5 py-0.5',
		lg: 'text-base px-3 py-1',
	};

	const variantClasses = {
		disabled: 'bg-gray-100 text-gray-500 border border-gray-200',
		blue: 'bg-blue-100 text-dyslexia-blue border border-blue-200',
		white: 'bg-white text-gray-700 border border-gray-200',
	};

	const clickableClasses = onClick
		? 'cursor-pointer hover:opacity-80 transition-opacity'
		: '';

	const classes = `${baseClasses} ${sizeClasses[size]} ${variantClasses[variant]} ${clickableClasses} ${className}`;

	return (
		<span
			className={classes}
			onClick={onClick}
			tabIndex={onClick ? 0 : undefined}
			onKeyDown={(event) => onKeyDown?.(event)}
		>
			{children}
		</span>
	);
}

export function StatusBadge({
	status,
	variant,
	size,
	className,
}: Omit<BadgeProps, 'children'> & { status: string }) {
	return (
		<Badge variant={variant} size={size} className={className}>
			{status}
		</Badge>
	);
}
