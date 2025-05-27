import { create } from 'zustand';

interface WritingData {
	initial?: string;
	medial?: string;
	final?: string;
	syllable?: string;
	word?: string;
}

interface WritingStore {
	writingData: Record<number, WritingData>;
	currentSyllableIndex: number;
	setWritingData: (index: number, data: Partial<WritingData>) => void;
	setCurrentSyllable: (index: number) => void;
	reset: () => void;
}

export const useWritingStore = create<WritingStore>((set) => ({
	writingData: {},
	currentSyllableIndex: 0,
	setWritingData: (index, data) =>
		set((state) => ({
			writingData: {
				...state.writingData,
				[index]: {
					...state.writingData[index],
					...data,
				},
			},
		})),
	setCurrentSyllable: (index) => set({ currentSyllableIndex: index }),
	reset: () => set({ writingData: {}, currentSyllableIndex: 0 }),
}));
