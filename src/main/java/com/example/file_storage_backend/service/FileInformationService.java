package com.example.file_storage_backend.service;

import com.example.file_storage_backend.dto.FileInformationDto;
import com.example.file_storage_backend.repository.FileRepository;
import com.example.file_storage_backend.utils.AppUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.persistence.oxm.MediaType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FileInformationService {

    final String REDIRECT_PARAMETER_NAME = "redirect";
    final String REDIRECT_PARAMETER_VALUE_BY_PUBLIC = "bypublic";
    final String REDIRECT_PARAMETER_VALUE_BY_OWNER = "byowner";

    @Inject
    FileRepository fileRepository;

    public void handleExchange(HttpServletRequest req, HttpServletResponse resp){
        resp.setContentType(MediaType.APPLICATION_JSON.getMediaType());
        String condition = req.getParameter(REDIRECT_PARAMETER_NAME);
        switch (condition){
            case REDIRECT_PARAMETER_VALUE_BY_PUBLIC:
                handleGetPublicFiles(resp);
                break;
            case REDIRECT_PARAMETER_VALUE_BY_OWNER:
                handleGetUserFiles(req, resp);
                break;
            default:
                handleDefault(resp);
        }
    }

    private void handleGetPublicFiles(HttpServletResponse resp){
        List<FileInformationDto> files = getPublicFiles();
        printJsonAsResponse(resp, files);
    }

    private List<FileInformationDto> getPublicFiles(){
        return fileRepository.getAllByPublic(true)
                .stream().map(AppUtils::fileEntityToDto)
                .collect(Collectors.toList());
    }

    private void handleGetUserFiles(HttpServletRequest req, HttpServletResponse resp){
        String owner = AppUtils.extractUser(req);
        if(owner==null){
            AppUtils.setStatus(resp, 408);
            return;
        }
        List<FileInformationDto> files = getOwnerFiles(owner);
        printJsonAsResponse(resp, files);
    }

    private List<FileInformationDto> getOwnerFiles(String owner){
        return fileRepository.getAllByOwner(owner)
                .stream().map(AppUtils::fileEntityToDto)
                .collect(Collectors.toList());
    }

    private void printJsonAsResponse(HttpServletResponse resp, List<FileInformationDto> files){
        try(ServletOutputStream servletOutput = resp.getOutputStream()){
            String json = parseFileListToJson(files);
            servletOutput.println(json);
        } catch (Exception e) {
            AppUtils.setStatus(resp,500);
        }
    }

    private String parseFileListToJson(List<FileInformationDto> files) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(files);
    }

    private void handleDefault(HttpServletResponse resp){
        AppUtils.setStatus(resp, 404);
    }
}
