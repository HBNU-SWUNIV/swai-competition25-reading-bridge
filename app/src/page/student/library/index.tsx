import { Typography } from "@/shared/ui/typography";
import { Card, CardContent } from "@/shared/ui/card";
import { Cover } from "@/shared/ui/cover";

type Book = {
  id: string;
  title: string;
  author: string;
  cover?: string;
};

const ALL_BOOKS: Book[] = [
  { id: "b1", title: "유체역학", author: "출판사1", cover: "/covers/1.jpg" },
  { id: "b2", title: "전자기학", author: "출판사2", cover: "/covers/2.jpg" },
  { id: "b3", title: "물리학", author: "출판사3", cover: "/covers/3.jpg" },
  { id: "b4", title: "화학", author: "출판사3", cover: "/covers/4.jpg" },
];

export default function LibraryView() {
  return (
    <section className="space-y-6">
      <Typography variant="h2" weight="semibold" size="xl" className="text-gray-800">
        전체 콘텐츠
      </Typography>

      <div className="flex space-x-4 overflow-x-auto pb-1">
        {ALL_BOOKS.map((book) => (
          <Card
            key={book.id}
            className="w-36 shrink-0 cursor-pointer hover:shadow-md transition-shadow"
          >
            <Cover
              src={book.cover}
              alt={book.title}
              className="h-52 w-full object-cover rounded-t"
            />
            <CardContent className="p-2">
              <Typography variant="p" size="sm" weight="medium" className="line-clamp-2">
                {book.title}
              </Typography>
              <Typography variant="p" size="sm" className="text-gray-500">
                {book.author}
              </Typography>
            </CardContent>
          </Card>
        ))}
      </div>
    </section>
  );
}
