package com.example.file_storage_backend.servlet;

import com.example.file_storage_backend.service.FileInformationService;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "FileInformationServlet", value = "/api/files/information")
public class FileInformationServlet extends HttpServlet {

    @Inject
    FileInformationService fileInformationService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        fileInformationService.handleExchange(req, resp);
    }
}
