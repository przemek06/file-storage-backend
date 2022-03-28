package com.example.file_storage_backend.service;

import com.example.file_storage_backend.entity.FileInformation;
import com.example.file_storage_backend.repository.FileRepository;
import com.example.file_storage_backend.utils.AppUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@ApplicationScoped
public class FileDownloadService {

    final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    final String CONTENT_DISPOSITION_HEADER_VALUE = "attachment; filename=";
    final String FILE_NOT_EXISTS_ERROR_MESSAGE = "This file doesn't exists";
    final String OCTET_STREAM_CONTENT_TYPE = "application/octet-stream";
    final String FILENAME_PARAMETER_NAME = "filename";

    @Inject
    FileRepository fileRepository;

    public void handleExchange(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletOutputStream servletOutput = resp.getOutputStream();
        File file = getFile(req);
        if (fileNotExisting(file, servletOutput)) {
            AppUtils.setStatus(resp, 404);
            servletOutput.close();
            return;
        }
        try {
            if(!isUserAuthorized(req, file)){
                throw new Exception();
            }
        } catch (Exception e) {
            AppUtils.setStatus(resp, 408);
            servletOutput.close();
            return;
        }
        setResponseHeaders(resp, file);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            sendFile(fileInputStream, servletOutput);
        } catch (IOException e) {
            AppUtils.setStatus(resp, 500);
        } finally {
            servletOutput.close();
        }
    }

    private boolean fileNotExisting(File file, ServletOutputStream servletOutput) {
        if (!file.exists()) {
            try {
                servletOutput.println(FILE_NOT_EXISTS_ERROR_MESSAGE);
            } catch (IOException ignored) {}
            return true;
        }
        return false;
    }

    private boolean isUserAuthorized(HttpServletRequest req, File file){
        FileInformation fileInformation = getFileInfo(file.getName());
        if(fileInformation.getPub()) return true;
        String username = AppUtils.extractUser(req);
        return fileInformation.getOwner().equals(username);
    }

    private FileInformation getFileInfo(String filename){
        return fileRepository.getByName(filename);
    }

    private File getFile(HttpServletRequest req) {
        String fileName = req.getParameter(FILENAME_PARAMETER_NAME);
        String path = Constants.FILE_PATH + File.separator + fileName;
        return new File(path);
    }

    private void setResponseHeaders(HttpServletResponse resp, File file) {
        resp.setContentType(OCTET_STREAM_CONTENT_TYPE);
        resp.setHeader(CONTENT_DISPOSITION_HEADER, CONTENT_DISPOSITION_HEADER_VALUE+"\"" + file.getName() + "\"");
    }

    private void sendFile(FileInputStream fileInputStream, ServletOutputStream servletOutput) throws IOException {
        byte[] buffer = new byte[1024];
        int data;
        while ((data = fileInputStream.read(buffer)) != -1) {
            servletOutput.write(buffer, 0, data);
        }
    }
}
