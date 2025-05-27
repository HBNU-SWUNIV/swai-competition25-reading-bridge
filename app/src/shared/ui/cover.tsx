// src/shared/ui/cover.tsx
import { useState } from "react";
import type { ImgHTMLAttributes } from "react";

interface CoverProps extends ImgHTMLAttributes<HTMLImageElement> {
  alt: string;
}

export function Cover({ src, alt, className = "", ...rest }: CoverProps) {
  const [error, setError] = useState(!src);

  if (error) {
    return (
      <div
        className={`flex h-44 w-full items-center justify-center bg-gray-100 text-xs text-gray-400 ${className}`}
      >
        이미지 없음
      </div>
    );
  }

  return (
    // biome-ignore lint/a11y/useAltText: <explanation>
<img
      src={src}
      alt={alt}
      className={`h-44 w-full object-cover ${className}`}
      onError={() => setError(true)}
      {...rest}
    />
  );
}
