import { useState } from "react"
import { createFileRoute } from "@tanstack/react-router"
import { AuthModal } from "@/features/auth/components/auth-modal"
import { BaseLayout } from "@/features/layouts/base-layout"
import { Hero } from "@/page/landing"
import { News } from "@/page/landing"
import { Introsection } from "@/page/landing"
import { Contact } from "@/page/landing"

export const Route = createFileRoute("/")({
  component: Landing,
})

export function Landing() {
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false)

  return (
    <BaseLayout onAuthClick={() => setIsAuthModalOpen(true)}>
      <section className="w-full flex flex-col gap-y-32">
        <Hero />
        <News />
        <Introsection />
        <Contact />
      </section>
      <AuthModal isOpen={isAuthModalOpen} onClose={() => setIsAuthModalOpen(false)} />
    </BaseLayout>
  )
}
