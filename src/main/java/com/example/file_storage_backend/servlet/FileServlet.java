package com.example.file_storage_backend.servlet;

import com.example.file_storage_backend.service.FileDownloadService;
import com.example.file_storage_backend.service.FileUploadService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "fileServlet", value = "/api/files/exchange", asyncSupported = true)
@MultipartConfig
public class FileServlet extends HttpServlet {

    @Inject
    FileUploadService fileUploadService;

    @Inject
    FileDownloadService fileDownloadService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        fileUploadService.handleExchange(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        fileDownloadService.handleExchange(req, resp);
    }
}
