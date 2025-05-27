package com.dyslexia.dyslexia.util;

public class DocumentProcessHolder {
    private static final ThreadLocal<Long> documentId = new ThreadLocal<>();
    private static final ThreadLocal<String> pdfName = new ThreadLocal<>();
    private static final ThreadLocal<String> teacherId = new ThreadLocal<>();
    private static final ThreadLocal<String> pdfFolderPath = new ThreadLocal<>();
    private static final ThreadLocal<Integer> pageNumber = new ThreadLocal<>();

    public static void setDocumentId(Long id) {
        documentId.set(id);
    }

    public static Long getDocumentId() {
        return documentId.get();
    }

    public static void setPdfName(String name) {
        pdfName.set(name);
    }

    public static String getPdfName() {
        return pdfName.get();
    }

    public static void setTeacherId(String id) {
        teacherId.set(id);
    }

    public static String getTeacherId() {
        return teacherId.get();
    }
    
    public static void setPdfFolderPath(String path) {
        pdfFolderPath.set(path);
    }
    
    public static String getPdfFolderPath() {
        return pdfFolderPath.get();
    }
    
    public static void setPageNumber(Integer number) {
        pageNumber.set(number);
    }
    
    public static Integer getPageNumber() {
        return pageNumber.get() != null ? pageNumber.get() : 1; // 기본값 1
    }

    public static void clear() {
        documentId.remove();
        pdfName.remove();
        teacherId.remove();
        pdfFolderPath.remove();
        pageNumber.remove();
    }
}
