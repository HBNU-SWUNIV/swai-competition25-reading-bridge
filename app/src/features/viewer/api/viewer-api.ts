import type {
	Document,
	PageContentResponse,
	PageTipResponse,
	PageImageResponse,
} from '../model/types';
import { axiosClient } from '@/shared/api/axios';

export const viewerApi = {
	getDocument: async (documentId: number): Promise<Document> => {
		return await axiosClient.get(`/documents/${documentId}`);
	},
	getPageContent: async (
		documentId: number,
		pageNumber?: number,
	): Promise<PageContentResponse[]> => {
		const params = pageNumber
			? { documentId, page: pageNumber }
			: { documentId };
		return await axiosClient.get('/document-contents/pages', { params });
	},
	getPageTips: async (pageId: number): Promise<PageTipResponse[]> => {
		return await axiosClient.get(`/document-contents/pages/${pageId}/tips`);
	},
	getPageImages: async (pageId: number): Promise<PageImageResponse[]> => {
		return await axiosClient.get(`/document-contents/pages/${pageId}/images`);
	},
};
