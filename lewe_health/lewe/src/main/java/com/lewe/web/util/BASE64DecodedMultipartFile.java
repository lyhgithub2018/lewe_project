package com.lewe.web.util;

import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
 
import java.io.*;
 
/**
 * base64转MultipartFile
 */
public class BASE64DecodedMultipartFile implements MultipartFile {
 
    private final byte[] imgContent;
    private final String header;
 
    public BASE64DecodedMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }
 
    public String getName() {
        // TODO - implementation depends on your requirements
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }
 
    public String getOriginalFilename() {
        // TODO - implementation depends on your requirements
        return System.currentTimeMillis() + (int) Math.random() * 10000 + "." + header.split("/")[1];
    }
 
    public String getContentType() {
        // TODO - implementation depends on your requirements
        return header.split(":")[1];
    }
 
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }
 
    public long getSize() {
        return imgContent.length;
    }
 
    public byte[] getBytes() throws IOException {
        return imgContent;
    }
 
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }
 
    @SuppressWarnings("resource")
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imgContent);
    }
 
    /**
     * base64转MultipartFile文件
     *
     * @param base64
     * @return
     */
    public static MultipartFile base64ToMultipart(String base64) {
        try {
            String[] baseStrs = base64.split(",");
 
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = new byte[0];
            b = decoder.decodeBuffer(baseStrs[1]);
            /*for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 2048;
                }
            }*/
            BASE64DecodedMultipartFile multipartFile = new BASE64DecodedMultipartFile(b, baseStrs[0]);
            return multipartFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

