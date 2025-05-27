import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { type SignUpFormData, type UserType, Grade } from '@/shared/types/auth'
import { useSignUp } from '@/shared/hooks/useSignUp'
import { Button } from '@/shared/ui/button'
import { Input } from '@/shared/ui/input'
import { Select } from '@/shared/ui/select'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/shared/ui/form'
import { Card, CardContent } from '@/shared/ui/card'
import { SuccessModal } from './success-modal'
import { useState } from 'react'
import { storage } from '@/shared/utils/storage'

const signUpSchema = z.object({
  name: z.string().min(2, '이름은 2자 이상이어야 합니다.'),
  grade: z.enum(['GRADE_1', 'GRADE_2', 'GRADE_3', 'GRADE_4', 'GRADE_5', 'GRADE_6'] as const).optional(),
  organization: z.string().min(1, '소속을 입력해주세요.'),
})

interface SignUpFormProps {
  userType: UserType
  defaultNickname?: string
}

export const SignUpForm = ({ 
  userType,
  defaultNickname,
}: SignUpFormProps) => {
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false)
  const { mutate: signUp, isPending } = useSignUp()
  const form = useForm<SignUpFormData>({
    resolver: zodResolver(signUpSchema),
    defaultValues: {
      name: defaultNickname || '',
      organization: '',
    },
  })

  const onSubmit = (data: SignUpFormData) => {
    const clientId = storage.getClientId()
    if (!clientId) {
      console.error('clientId가 없습니다.')
      return
    }

    signUp(
      {
        ...data,
        userType,
        clientId,
        type: userType,
        interestIds: [],
      },
      {
        onSuccess: () => {
          storage.removeClientId()
          setIsSuccessModalOpen(true)
        },
      }
    )
  }

  return (
    <>
      <Card className="w-full max-w-md mx-auto">
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              <FormField
                control={form.control}
                name="name"
                render={({ field, fieldState, formState }) => (
                  <FormItem>
                    <FormLabel className="text-base">이름</FormLabel>
                    <FormControl>
                      <Input 
                        placeholder="이름을 입력하세요" 
                        {...field} 
                        className="h-12 text-base"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {userType === 'STUDENT' && (
                <FormField
                  control={form.control}
                  name="grade"
                  render={({ field, fieldState, formState }) => (
                    <FormItem>
                      <FormLabel className="text-base">학년</FormLabel>
                      <Select
                        onChange={field.onChange}
                        value={field.value}
                        className="h-12 text-base"
                      >
                        <option value="">학년을 선택하세요</option>
                        <option value="GRADE_1">1학년</option>
                        <option value="GRADE_2">2학년</option>
                        <option value="GRADE_3">3학년</option>
                        <option value="GRADE_4">4학년</option>
                        <option value="GRADE_5">5학년</option>
                        <option value="GRADE_6">6학년</option>
                      </Select>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              )}

              <FormField
                control={form.control}
                name="organization"
                render={({ field, fieldState, formState }) => (
                  <FormItem>
                    <FormLabel className="text-base">소속</FormLabel>
                    <FormControl>
                      <Input 
                        placeholder="소속을 입력하세요" 
                        {...field} 
                        className="h-12 text-base"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Button 
                type="submit" 
                disabled={isPending}
                className="w-full h-12 text-base font-semibold"
              >
                {isPending ? '처리 중...' : '회원가입'}
              </Button>
            </form>
          </Form>
        </CardContent>
      </Card>

      <SuccessModal
        isOpen={isSuccessModalOpen}
        onOpenChange={setIsSuccessModalOpen}
        userType={userType}
      />
    </>
  )
} 