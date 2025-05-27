import type React from 'react';
import { useRef, useEffect, useState, forwardRef, useCallback } from 'react';
import { Button } from '@/shared/ui/button';
import { Eraser } from 'lucide-react';
import { cn } from '@/lib/utils';

interface WritingCanvasProps extends React.HTMLAttributes<HTMLCanvasElement> {
	height?: number;
	width?: number;
	onSave?: (pngData: string) => void;
	initialImage?: string;
	guideText?: string;
	guideTextScale?: number;
}

export const WritingCanvas = forwardRef<HTMLCanvasElement, WritingCanvasProps>(
	({ className, height = 300, width, onSave, initialImage, guideText, guideTextScale = 1, ...props }, ref) => {
		const canvasRef = useRef<HTMLCanvasElement | null>(null);
		const guideCanvasRef = useRef<HTMLCanvasElement | null>(null);
		const contextRef = useRef<CanvasRenderingContext2D | null>(null);
		const guideContextRef = useRef<CanvasRenderingContext2D | null>(null);
		const containerRef = useRef<HTMLDivElement>(null);
		const [isDrawing, setIsDrawing] = useState(false);
		const [containerWidth, setContainerWidth] = useState(0);
		const lastPointRef = useRef<{ x: number; y: number; pressure: number } | null>(null);
		const pointsRef = useRef<{ x: number; y: number; pressure: number }[]>([]);
		const saveTimeoutRef = useRef<number | null>(null);

		const handleSave = useCallback(() => {
			if (onSave && canvasRef.current) {
				const pngData = canvasRef.current.toDataURL('image/png');
				onSave(pngData);
			}
		}, [onSave]);

		useEffect(() => {
			const updateCanvasSize = () => {
				const container = containerRef.current;
				if (!container) return;
				setContainerWidth(width || container.clientWidth);
			};

			updateCanvasSize();
			if (!width) {
				const resizeObserver = new ResizeObserver(updateCanvasSize);
				if (containerRef.current) {
					resizeObserver.observe(containerRef.current);
				}
				return () => resizeObserver.disconnect();
			}
		}, [width]);

		// 가이드 텍스트 렌더링 함수
		const renderGuideText = useCallback(() => {
			const guideContext = guideContextRef.current;
			if (!guideContext || !containerWidth) return;

			// 캔버스 초기화
			guideContext.clearRect(0, 0, containerWidth, height);
			
			if (guideText) {
				// 가이드 텍스트 스타일 설정
				guideContext.fillStyle = '#64748b';
				guideContext.globalAlpha = 0.3;
				
				// 텍스트 크기 조정
				const fontSize = Math.min(height * 0.8, containerWidth * 0.8) * guideTextScale;
				guideContext.font = `${fontSize}px "Noto Sans KR"`;
				guideContext.textAlign = 'center';
				guideContext.textBaseline = 'middle';
				
				// 텍스트 렌더링
				guideContext.fillText(guideText, containerWidth / 2, height / 2);
			}
		}, [guideText, containerWidth, height, guideTextScale]);

		useEffect(() => {
			const canvas = ref ? (ref as React.RefObject<HTMLCanvasElement>).current : canvasRef.current;
			const guideCanvas = guideCanvasRef.current;
			if (!canvas || !guideCanvas || containerWidth === 0) return;

			const scale = window.devicePixelRatio;
			
			// 가이드 캔버스 설정
			guideCanvas.width = containerWidth * scale;
			guideCanvas.height = height * scale;
			guideCanvas.style.width = `${containerWidth}px`;
			guideCanvas.style.height = `${height}px`;

			const guideContext = guideCanvas.getContext('2d', { alpha: true });
			if (!guideContext) return;

			guideContext.scale(scale, scale);
			guideContextRef.current = guideContext;
			
			// 메인 캔버스 설정
			canvas.width = containerWidth * scale;
			canvas.height = height * scale;
			canvas.style.width = `${containerWidth}px`;
			canvas.style.height = `${height}px`;

			const context = canvas.getContext('2d', { alpha: true });
			if (!context) return;

			context.scale(scale, scale);
			context.lineCap = 'round';
			context.lineJoin = 'round';
			context.strokeStyle = '#000000';
			context.shadowColor = '#000000';
			context.shadowBlur = 1;
			contextRef.current = context;

			// 초기 이미지가 있다면 로드
			if (initialImage) {
				const img = new Image();
				img.onload = () => {
					if (context) {
						context.drawImage(img, 0, 0);
					}
				};
				img.src = initialImage;
			}

			// 가이드 텍스트 렌더링
			renderGuideText();
		}, [ref, containerWidth, height, initialImage, renderGuideText]);

		// 가이드 텍스트가 변경될 때마다 다시 렌더링
		useEffect(() => {
			if (guideContextRef.current) {
				renderGuideText();
			}
		}, [renderGuideText]);

		const getStrokeWidth = (pressure: number) => {
			const minWidth = 3;
			const maxWidth = 6.5;
			return minWidth + (maxWidth - minWidth) * pressure;
		};

		const drawPoints = (points: { x: number; y: number; pressure: number }[]) => {
			const context = contextRef.current;
			if (!context || points.length < 2) return;

			context.beginPath();
			context.moveTo(points[0].x, points[0].y);

			for (let i = 1; i < points.length - 2; i++) {
				const c = (points[i].x + points[i + 1].x) / 2;
				const d = (points[i].y + points[i + 1].y) / 2;
				const pressure = (points[i].pressure + points[i + 1].pressure) / 2;

				context.lineWidth = getStrokeWidth(pressure);
				context.quadraticCurveTo(points[i].x, points[i].y, c, d);
			}

			if (points.length > 2) {
				const lastPoint = points[points.length - 1];
				const preLastPoint = points[points.length - 2];
				context.lineWidth = getStrokeWidth(lastPoint.pressure);
				context.quadraticCurveTo(
					preLastPoint.x,
					preLastPoint.y,
					lastPoint.x,
					lastPoint.y,
				);
			}

			context.stroke();
		};

		const getEventPoint = (e: React.PointerEvent<HTMLCanvasElement>) => {
			const rect = e.currentTarget.getBoundingClientRect();
			return {
				x: e.clientX - rect.left,
				y: e.clientY - rect.top,
				pressure: e.pressure !== 0 ? e.pressure : 0.5,
			};
		};

		const startDrawing = (e: React.PointerEvent<HTMLCanvasElement>) => {
			const point = getEventPoint(e);
			setIsDrawing(true);
			lastPointRef.current = point;
			pointsRef.current = [point];
		};

		const draw = (e: React.PointerEvent<HTMLCanvasElement>) => {
			if (!isDrawing || !contextRef.current || !lastPointRef.current) return;
			e.preventDefault();

			const point = getEventPoint(e);
			pointsRef.current.push(point);

			if (pointsRef.current.length > 4) {
				drawPoints(pointsRef.current.slice(-4));
				pointsRef.current = pointsRef.current.slice(-3);
			}

			lastPointRef.current = point;
		};

		const stopDrawing = () => {
			if (!contextRef.current) return;

			if (pointsRef.current.length >= 2) {
				drawPoints(pointsRef.current);
			}

			setIsDrawing(false);
			lastPointRef.current = null;
			pointsRef.current = [];

			if (saveTimeoutRef.current) {
				window.clearTimeout(saveTimeoutRef.current);
			}
			saveTimeoutRef.current = window.setTimeout(handleSave, 300);
		};

		const clearCanvas = () => {
			const context = contextRef.current;
			const canvas = canvasRef.current;
			if (!context || !canvas) return;

			const scale = window.devicePixelRatio;
			
			// 메인 캔버스 초기화
			canvas.width = containerWidth * scale;
			canvas.height = height * scale;
			context.scale(scale, scale);
			context.lineCap = 'round';
			context.lineJoin = 'round';
			context.strokeStyle = '#000000';
			context.shadowColor = '#000000';
			context.shadowBlur = 1;

			// 가이드 텍스트 다시 렌더링
			renderGuideText();

			handleSave();
		};

		return (
			<div 
				className="flex flex-col gap-4" 
				ref={containerRef}
				style={width ? { width: `${width}px` } : undefined}
			>
				<div 
					className="relative rounded-lg border-2 border-dyslexia-blue shadow-sm w-full overflow-hidden"
					style={{ 
						height: `${height}px`,
						padding: '2px'
					}}
				>
					<canvas
						ref={guideCanvasRef}
						className={cn('touch-none absolute top-0 left-0 z-0', className)}
						style={{ background: 'transparent' }}
					/>
					<canvas
						ref={ref || canvasRef}
						onPointerDown={startDrawing}
						onPointerMove={draw}
						onPointerUp={stopDrawing}
						onPointerOut={stopDrawing}
						className={cn('touch-none absolute top-0 left-0 z-10', className)}
						style={{ background: 'transparent' }}
						{...props}
					/>
				</div>
				<Button
					variant="outline"
					size="sm"
					onClick={clearCanvas}
					className="w-fit"
				>
					<Eraser className="mr-2 h-4 w-4" />
					다시 쓰기
				</Button>
			</div>
		);
	},
);
