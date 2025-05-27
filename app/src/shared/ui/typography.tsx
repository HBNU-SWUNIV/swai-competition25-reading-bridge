import { type VariantProps, cva } from "class-variance-authority";
import { cn } from "@/shared/lib/utils";
import { type ElementType, forwardRef } from "react";
const typographyVariants = cva("", {
  variants: {
    variant: {
      h1: "scroll-m-20 text-4xl font-extrabold tracking-tight lg:text-5xl text-1B98FF",
      h2: "scroll-m-20 text-3xl font-semibold tracking-tight text-1B98FF",
      h3: "scroll-m-20 text-2xl font-semibold tracking-tight text-1B98FF",
      h4: "scroll-m-20 text-xl font-semibold tracking-tight text-1B98FF",
      p: "leading-7",
      blockquote: "mt-6 border-l-2 pl-6 italic",
      list: "ml-6 list-disc [&>li]:mt-2",
    },
    size: {
      sm: "text-sm",
      base: "text-base",
      lg: "text-lg",
      xl: "text-xl",
      "2xl": "text-2xl",
    },
    weight: {
      normal: "font-normal",
      medium: "font-medium",
      semibold: "font-semibold",
      bold: "font-bold",
    },
    align: {
      left: "text-left",
      center: "text-center",
      right: "text-right",
    },
    color: {
      "dyslexia-blue": "text-dyslexia-blue",
      primary: "text-dyslexia-blue",
      secondary: "text-brand-secondary",
      muted: "text-brand-muted",
      destructive: "text-brand-destructive",
    },
  },
  defaultVariants: {
    variant: "p",
    size: "base",
    weight: "normal",
    align: "left",
    color: "primary",
  },
});

interface TypographyProps
  extends React.HTMLAttributes<HTMLParagraphElement>,
    VariantProps<typeof typographyVariants> {
  asChild?: boolean;
  as?: ElementType;
  color?: "primary" | "secondary" | "muted" | "destructive";
}

const Typography = forwardRef<HTMLParagraphElement, TypographyProps>(
  ({ 
    className, 
    variant, 
    size, 
    weight, 
    align, 
    color, 
    as, 
    children, 
    ...props 
  }, ref) => {
    const Component = as || "p";
    return (
      <Component
        ref={ref}
        className={cn(
          typographyVariants({ 
            variant, 
            size, 
            weight, 
            align, 
            color: color as "primary" | "secondary" | "muted" | "destructive" | undefined 
          }), 
          className
        )}
        {...props}
      >
        {children}
      </Component>
    );
  }
);

Typography.displayName = "Typography";

export { Typography, typographyVariants };
export type { TypographyProps };
