package com.example.file_storage_backend.service;

import com.example.file_storage_backend.repository.FileRepository;
import com.example.file_storage_backend.utils.AppUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;

@ApplicationScoped
public class FileUploadService {

    final String IS_PUBLIC_PARAM_NAME = "public";
    final String FILE_DELETE_ERROR_MESSAGE = "Couldn't delete a file: ";
    final String SUCCESS_UPLOAD_MESSAGE = "File uploaded!";
    final String FILE_EXISTS_ERROR_MESSAGE = "There is already a file with this name!";

    @Inject
    FileRepository fileRepository;

    public void handleExchange(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        final Part part = extractPart(req);
        String fileName = part.getSubmittedFileName();
        File file = getFile(fileName);

        InputStream servletInput = part.getInputStream();
        ServletOutputStream servletOutput = resp.getOutputStream();

        if (fileExists(file, servletOutput)) {
            AppUtils.setStatus(resp, 400);
            return;
        }
        try (final OutputStream fileOutput = new FileOutputStream(file)) {
            downloadFile(servletInput, fileOutput, servletOutput);
            String owner = AppUtils.extractUser(req);
            boolean successfulSave = saveToDatabase(fileName, part, isPublic(req), owner);
            if(!successfulSave){
                handleDatabaseError(resp, file);
            }
        } catch (IOException ex) {
            AppUtils.setStatus(resp, 500);
        } finally {
            servletOutput.close();
        }
    }

    private boolean saveToDatabase(String fileName, Part part, boolean pub, String owner) {
        return fileRepository.addFile(fileName, owner, part.getSize(), pub);
    }

    private void handleDatabaseError(HttpServletResponse resp, File file) {
        AppUtils.setStatus(resp, 500);
        if (!file.delete()) {
            System.out.println(FILE_DELETE_ERROR_MESSAGE + file.getName());
        }

    }

    private void downloadFile(InputStream input, OutputStream fileOutput, ServletOutputStream servletOutput)
            throws IOException {
        final byte[] bytes = new byte[1024];
        int read;
        while ((read = input.read(bytes)) != -1) {
            fileOutput.write(bytes, 0, read);
        }
        servletOutput.println(SUCCESS_UPLOAD_MESSAGE);
    }

    private boolean fileExists(File file, ServletOutputStream output) throws IOException {
        if (file.exists()) {
            try {
                output.println(FILE_EXISTS_ERROR_MESSAGE);
            } catch (IOException e) {
                output.close();
            }
            return true;
        }
        return false;
    }

    private boolean isPublic(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter(IS_PUBLIC_PARAM_NAME));
    }

    private Part extractPart(HttpServletRequest request) throws IOException, ServletException {
        return request.getPart(Constants.PART_NAME);
    }

    private File getFile(String fileName) {
        String path = Constants.FILE_PATH + File.separator + fileName;
        return new File(path);
    }


}
