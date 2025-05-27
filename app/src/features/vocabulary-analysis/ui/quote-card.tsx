import { Quote } from 'lucide-react';
import { Card, CardContent } from '@/shared/ui/card';

interface QuoteCardProps {
	text: string;
}

export function QuoteCard({ text }: QuoteCardProps) {
	return (
		<Card className="mb-4 bg-muted/50">
			<CardContent className="">
				<div className="flex gap-2">
					<Quote className="h-6 w-6 text-muted-foreground" />
					<p className="text-muted-foreground italic">{text}</p>
				</div>
			</CardContent>
		</Card>
	);
} 