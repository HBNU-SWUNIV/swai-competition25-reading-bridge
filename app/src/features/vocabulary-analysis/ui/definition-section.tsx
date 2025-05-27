import { ChevronDown } from 'lucide-react';
import { Button } from '@/shared/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/shared/ui/card';
import {
	Collapsible,
	CollapsibleContent,
	CollapsibleTrigger,
} from "@/shared/ui/collapsible"
import { useTextToSpeech } from '@/shared/hooks/use-text-to-speech';

interface DefinitionSectionProps {
	word: string;
	definition: string;
	simplifiedDefinition: string;
	examples: string;
	isOpen: boolean;
	onOpenChange: (open: boolean) => void;
}

export function DefinitionSection({
	word,
	definition,
	simplifiedDefinition,
	examples,
	isOpen,
	onOpenChange,
}: DefinitionSectionProps) {
	const { speak } = useTextToSpeech();
	const exampleList = examples.replace(/[\[\]"]/g, '').split(',').map(example => example.trim());

	return (
		<div className="space-y-4">
			<Card>
				<Collapsible open={isOpen} onOpenChange={onOpenChange}>
					<CardHeader className="pb-0">
						<div className="flex items-center justify-between">
							<CardTitle className="text-lg">쉽게 풀어보는 "{word}"</CardTitle>
							<CollapsibleTrigger asChild>
								<Button variant="ghost" size="sm" className="h-8 w-8 p-0">
									<ChevronDown className={`h-4 w-4 transition-transform duration-200 ${isOpen ? "" : "-rotate-90"}`} />
								</Button>
							</CollapsibleTrigger>
						</div>
					</CardHeader>
					<CollapsibleContent>
						<CardContent className="space-y-4 pt-4">
							<div>
								<h4 className="mb-2 font-semibold">의미</h4>
								<p className="text-muted-foreground">{simplifiedDefinition}</p>
							</div>
							<div>
								<h4 className="mb-2 font-semibold">자세한 설명</h4>
								<p className="text-sm text-muted-foreground">{definition}</p>
							</div>
							<div>
								<h4 className="mb-2 font-semibold">이렇게 사용해요!</h4>
								<div className="space-y-2">
									{exampleList.map((example, index) => (
										<button
											key={example}
											type="button"
											onClick={(e) => {
												e.preventDefault();
												speak(example);
											}}
										>
										<div 
											className="rounded-lg bg-amber-50 p-4"
										>
											<p className="text-orange-700">{example}</p>
										</div>											
										</button>

									))}
								</div>
							</div>
						</CardContent>
					</CollapsibleContent>
				</Collapsible>
			</Card>
		</div>
	);
} 