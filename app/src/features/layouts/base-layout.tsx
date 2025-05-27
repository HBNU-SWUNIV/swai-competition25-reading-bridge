import type { ReactNode } from "react"
import Header from "./header"

interface BaseLayoutProps {
  children: ReactNode
  onAuthClick: () => void
}

export const BaseLayout = ({ children, onAuthClick }: BaseLayoutProps) => {
  return (
    <div className="min-h-screen">
      <Header onAuthClick={onAuthClick} />
      <main className="flex-1 pt-14">
        {children}
      </main>
    </div>
  )
} 