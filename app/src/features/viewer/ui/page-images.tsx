import type { PageImageResponse } from '../model/types';
import { Typography } from '@/shared/ui';
import type React from 'react';

interface PageImagesProps {
	images: PageImageResponse[];
}

export const PageImages: React.FC<PageImagesProps> = ({ images }) => {
	if (!images || images.length === 0) return null;

	return (
		<div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
			{images.map((image) => (
				<div
					key={image.id}
					className="bg-white rounded-md overflow-hidden shadow-sm"
				>
					<img
						src={image.imageUrl}
						alt={image.altText || '이미지'}
						className="w-full h-auto"
					/>
					<div className="p-3">
						<Typography variant="p" className="font-medium">
							{image.conceptReference}
						</Typography>
						<Typography variant="p" className="text-gray-600 text-sm mt-1">
							{image.altText}
						</Typography>
					</div>
				</div>
			))}
		</div>
	);
};
