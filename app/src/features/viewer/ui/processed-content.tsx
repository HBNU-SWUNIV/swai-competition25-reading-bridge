import type { Block } from '../model/types';
import { parseBlocks } from '../lib/parse-blocks';
import { useBlockSpeech } from '../lib/use-block-speech';
import type React from 'react';

function splitBlocksIntoColumns(blocks: Block[]): [Block[], Block[]] {
	const midpoint = Math.ceil(blocks.length / 2);
	return [blocks.slice(0, midpoint), blocks.slice(midpoint)];
}

interface ProcessedContentProps {
	blocks: Block[];
	fontSize: number;
	fontFamily: string;
	lineSpacing: number;
	documentId: number;
	pageNumber: number;
}

export const ProcessedContent: React.FC<ProcessedContentProps> = ({
	blocks,
	fontSize,
	fontFamily,
	lineSpacing,
	documentId,
	pageNumber,
}) => {
	const { activeBlockId, speak } = useBlockSpeech();
	const [leftBlocks, rightBlocks] = splitBlocksIntoColumns(blocks);

	const handleBlockClick = (block: Block) => {
		console.log({ block })
		if ('text' in block && block.text) {
			speak(block.id, block.text);
		}
	};

	const blockOptions = {
		fontSize,
		fontFamily,
		lineSpacing,
		onBlockClick: handleBlockClick,
		activeBlockId,
		documentId,
		pageNumber,
	};

	return (
		<div className="flex flex-row gap-x-4 items-stretch">
			<div className="flex-1">
				{parseBlocks(leftBlocks, blockOptions)}
			</div>
			<div className="w-[2px] bg-slate-100 mx-2 self-stretch" />
			<div className="flex-1">
				{parseBlocks(rightBlocks, blockOptions)}
			</div>
		</div>
	);
};
