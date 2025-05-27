import { useCallback, useRef } from 'react';

interface UseLongPressOptions {
  onClick?: () => void;
  onLongPress: () => void;
  onStart?: () => void;
  onFinish?: () => void;
  onCancel?: () => void;
  threshold?: number;
}

export function useLongPress({
  onClick,
  onLongPress,
  onStart,
  onFinish,
  onCancel: onCancelProp,
  threshold = 1000,
}: UseLongPressOptions) {
  const timerRef = useRef<NodeJS.Timeout | null>(null);
  const isLongPress = useRef(false);
  const isPressed = useRef(false);

  const start = useCallback(
    (event: React.MouseEvent | React.TouchEvent) => {
      if ('button' in event && event.button !== 0) {
        return;
      }

      isPressed.current = true;
      isLongPress.current = false;
      onStart?.();

      timerRef.current = setTimeout(() => {
        if (isPressed.current) {
          isLongPress.current = true;
          onLongPress();
        }
      }, threshold);
    },
    [onLongPress, onStart, threshold],
  );

  const stop = useCallback(
    (event: React.MouseEvent | React.TouchEvent) => {
      isPressed.current = false;
      onFinish?.();
      
      if (timerRef.current) {
        clearTimeout(timerRef.current);
        if (!isLongPress.current && onClick) {
          onClick();
        }
      }
    },
    [onClick, onFinish],
  );

  const cancel = useCallback(() => {
    isPressed.current = false;
    onCancelProp?.();
    if (timerRef.current) {
      clearTimeout(timerRef.current);
    }
  }, [onCancelProp]);

  return {
    onMouseDown: start,
    onMouseUp: stop,
    onMouseLeave: cancel,
    onTouchStart: start,
    onTouchEnd: stop,
    onTouchCancel: cancel,
  };
} 