import type { FC, ReactNode } from 'react'

interface ShowProps {
  when: boolean
  children: ReactNode
}

export const Show: FC<ShowProps> = ({ when, children }) => {
  if (!when) return null;
  return <>{children}</>
};
