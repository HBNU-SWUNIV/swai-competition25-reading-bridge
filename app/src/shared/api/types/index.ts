export * from './vocabulary';

export interface PhonemeComponent {
	consonant: string;
	pronunciation: string;
	sound: string;
	difficulty: string;
}

export interface SyllableComponents {
	initial: PhonemeComponent;
	medial: {
		vowel: string;
		pronunciation: string;
		sound: string;
		difficulty: string;
	};
	final?: PhonemeComponent;
}

export interface SyllableInfo {
	syllable: string;
	combinedSound: string;
	writingTips: string;
	components: SyllableComponents;
}

export interface WritingStep {
	step: number;
	syllable: string;
	phoneme: string;
	description: string;
}

export interface LearningTips {
	commonMistakes: string[];
	practiceWords: string[];
	rhymingWords: string[];
}

export interface PhonemeAnalysis {
	syllables: SyllableInfo[];
	writingOrder: WritingStep[];
	learningTips: LearningTips;
} 